package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.UsuarioDAO;
import br.com.fiap.lotamenos.dto.UsuarioRequest;
import br.com.fiap.lotamenos.dto.UsuarioResponse;
import br.com.fiap.lotamenos.exception.RecursoNaoEncontradoException;
import br.com.fiap.lotamenos.exception.RegraNegocioException;
import br.com.fiap.lotamenos.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Regras dos usuários.
 * <ul>
 *   <li>E-mail único (sem diferenciar maiúsculas/minúsculas), gravado em minúsculas.</li>
 *   <li>Todo usuário começa com reputação 1.0; ela só muda pelos relatos ({@link RelatoLotacaoBO}),
 *       nunca pelo PUT.</li>
 *   <li>A data de cadastro é definida pelo servidor.</li>
 *   <li>Excluir usuário com relatos ou consultas gera conflito (409).</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class UsuarioBO {

    private final UsuarioDAO usuarioDAO;
    private final Clock clock;

    public UsuarioBO(UsuarioDAO usuarioDAO, Clock clock) {
        this.usuarioDAO = usuarioDAO;
        this.clock = clock;
    }

    public List<UsuarioResponse> listar() {
        return usuarioDAO.findAllByOrderByIdAsc().stream().map(UsuarioResponse::de).toList();
    }

    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.de(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest req) {
        String email = req.email().trim().toLowerCase();
        if (usuarioDAO.existsByEmailIgnoreCase(email)) {
            throw new RegraNegocioException("Já existe um usuário com o e-mail " + email + ".");
        }
        Usuario usuario = new Usuario(req.nome().trim(), email, req.perfil(), Usuario.REPUTACAO_INICIAL,
                LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS));
        return UsuarioResponse.de(usuarioDAO.save(usuario));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest req) {
        Usuario usuario = buscarEntidade(id);
        String email = req.email().trim().toLowerCase();
        if (usuarioDAO.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new RegraNegocioException("Já existe outro usuário com o e-mail " + email + ".");
        }
        usuario.setNome(req.nome().trim());
        usuario.setEmail(email);
        usuario.setPerfil(req.perfil());
        return UsuarioResponse.de(usuarioDAO.save(usuario));
    }

    @Transactional
    public void excluir(Long id) {
        Usuario usuario = buscarEntidade(id);
        usuarioDAO.delete(usuario);
        usuarioDAO.flush(); // FK violada → DataIntegrityViolationException (409)
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioDAO.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário com id " + id + " não encontrado."));
    }
}
