package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.EstacaoDAO;
import br.com.fiap.lotamenos.dao.LinhaDAO;
import br.com.fiap.lotamenos.dao.LinhaEstacaoDAO;
import br.com.fiap.lotamenos.dao.RelatoLotacaoDAO;
import br.com.fiap.lotamenos.dao.UsuarioDAO;
import br.com.fiap.lotamenos.dto.RelatoRequest;
import br.com.fiap.lotamenos.dto.RelatoResponse;
import br.com.fiap.lotamenos.dto.RelatoUpdateRequest;
import br.com.fiap.lotamenos.exception.EstacaoForaDoRecorteException;
import br.com.fiap.lotamenos.exception.RecursoNaoEncontradoException;
import br.com.fiap.lotamenos.exception.RegraNegocioException;
import br.com.fiap.lotamenos.exception.RelatoDuplicadoException;
import br.com.fiap.lotamenos.model.Estacao;
import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.RelatoLotacao;
import br.com.fiap.lotamenos.model.Usuario;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class RelatoLotacaoBO {
    public static final int INTERVALO_ANTISPAM_MINUTOS = 10;
    public static final int TOLERANCIA_COERENCIA = 20;
    public static final double BONUS_REPUTACAO = 0.05;
    public static final double PENALIDADE_REPUTACAO = 0.10;

    private final RelatoLotacaoDAO relatoDAO;
    private final UsuarioDAO usuarioDAO;
    private final EstacaoDAO estacaoDAO;
    private final LinhaDAO linhaDAO;
    private final LinhaEstacaoDAO linhaEstacaoDAO;
    private final LotacaoBO lotacaoBO;
    private final Clock clock;

    public RelatoLotacaoBO(RelatoLotacaoDAO relatoDAO, UsuarioDAO usuarioDAO, EstacaoDAO estacaoDAO,
                           LinhaDAO linhaDAO, LinhaEstacaoDAO linhaEstacaoDAO, LotacaoBO lotacaoBO, Clock clock) {
        this.relatoDAO = relatoDAO;
        this.usuarioDAO = usuarioDAO;
        this.estacaoDAO = estacaoDAO;
        this.linhaDAO = linhaDAO;
        this.linhaEstacaoDAO = linhaEstacaoDAO;
        this.lotacaoBO = lotacaoBO;
        this.clock = clock;
    }

    public List<RelatoResponse> listar(Long estacaoId, Integer ultimosMinutos) {
        LocalDateTime desde = LocalDateTime.of(1900, 1, 1, 0, 0);
        if (ultimosMinutos != null) {
            if (ultimosMinutos <= 0) {
                throw new RegraNegocioException("O filtro 'ultimosMinutos' deve ser maior que zero.");
            }
            desde = LocalDateTime.now(clock).minusMinutes(ultimosMinutos);
        }
        return relatoDAO.filtrar(estacaoId, desde).stream().map(RelatoResponse::de).toList();
    }

    public RelatoResponse buscar(Long id) {
        return RelatoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public RelatoResponse registrar(RelatoRequest req) {
        Usuario usuario = usuarioDAO.findById(req.usuarioId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário com id " + req.usuarioId() + " não encontrado."));
        Estacao estacao = estacaoDAO.findById(req.estacaoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Estação com id " + req.estacaoId() + " não encontrada."));
        Linha linha = resolverLinha(req.linhaId());
        if (!linhaEstacaoDAO.existsByLinhaIdAndEstacaoId(linha.getId(), estacao.getId())) {
            throw EstacaoForaDoRecorteException.daEstacao(estacao.getNome());
        }

        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime dataHora = req.dataHora() != null ? req.dataHora().truncatedTo(ChronoUnit.SECONDS) : agora;
        if (dataHora.isAfter(agora.plusMinutes(1))) {
            throw new RegraNegocioException("A data/hora do relato não pode estar no futuro.");
        }

        if (relatoDAO.existeRelatoNoIntervalo(usuario.getId(), estacao.getId(),
                dataHora.minusMinutes(INTERVALO_ANTISPAM_MINUTOS), dataHora.plusMinutes(INTERVALO_ANTISPAM_MINUTOS))) {
            throw new RelatoDuplicadoException("Você já relatou a lotação da estação " + estacao.getNome()
                    + " há menos de " + INTERVALO_ANTISPAM_MINUTOS + " minutos. Aguarde para relatar novamente.");
        }

        int indiceAtual = lotacaoBO.calcularIndice(estacao, linha, req.sentido(), agora);
        usuario.setReputacao(novaReputacao(usuario.getReputacao(), req.nivel().getValor(), indiceAtual));
        usuarioDAO.save(usuario);

        RelatoLotacao relato = new RelatoLotacao(usuario, estacao, linha, req.sentido(), req.nivel(), dataHora);
        return RelatoResponse.de(relatoDAO.save(relato));
    }

    @Transactional
    public RelatoResponse atualizar(Long id, RelatoUpdateRequest req) {
        RelatoLotacao relato = buscarEntidade(id);
        relato.setSentido(req.sentido());
        relato.setNivel(req.nivel());
        return RelatoResponse.de(relatoDAO.save(relato));
    }

    @Transactional
    public void excluir(Long id) {
        relatoDAO.delete(buscarEntidade(id));
    }

    static double novaReputacao(double reputacaoAtual, int valorRelatado, int indiceAtual) {
        double ajuste = Math.abs(valorRelatado - indiceAtual) <= TOLERANCIA_COERENCIA
                ? BONUS_REPUTACAO
                : -PENALIDADE_REPUTACAO;
        double nova = Math.round((reputacaoAtual + ajuste) * 100) / 100.0;
        return Math.max(Usuario.REPUTACAO_MINIMA, Math.min(Usuario.REPUTACAO_MAXIMA, nova));
    }

    private Linha resolverLinha(Long linhaId) {
        if (linhaId == null) {
            return linhaDAO.findByNumero(LinhaBO.NUMERO_LINHA_RECORTE)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("A Linha 3-Vermelha não está cadastrada."));
        }
        Linha linha = linhaDAO.findById(linhaId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Linha com id " + linhaId + " não encontrada."));
        if (linha.getNumero() != LinhaBO.NUMERO_LINHA_RECORTE) {
            throw new EstacaoForaDoRecorteException("Relatos são aceitos apenas na Linha 3-Vermelha "
                    + "(recorte do LotaMenos). A Linha " + linha.getNumero() + "-" + linha.getNome()
                    + " está fora do recorte.");
        }
        return linha;
    }

    private RelatoLotacao buscarEntidade(Long id) {
        return relatoDAO.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Relato com id " + id + " não encontrado."));
    }
}
