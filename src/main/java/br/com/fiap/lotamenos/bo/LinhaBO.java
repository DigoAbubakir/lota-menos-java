package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.LinhaDAO;
import br.com.fiap.lotamenos.dao.LinhaEstacaoDAO;
import br.com.fiap.lotamenos.dto.EstacaoNaLinhaResponse;
import br.com.fiap.lotamenos.dto.LinhaRequest;
import br.com.fiap.lotamenos.dto.LinhaResponse;
import br.com.fiap.lotamenos.exception.RecursoNaoEncontradoException;
import br.com.fiap.lotamenos.exception.RegraNegocioException;
import br.com.fiap.lotamenos.model.Linha;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Regras das linhas.
 * <ul>
 *   <li>O número da linha é único (não pode haver duas "Linha 3").</li>
 *   <li>A Linha 3-Vermelha ({@link #NUMERO_LINHA_RECORTE}) é o recorte do projeto: planejamento de viagem
 *       e relatos só funcionam nela.</li>
 *   <li>Excluir uma linha com estações/histórico/relatos vinculados gera conflito (409).</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class LinhaBO {

    /** Número da linha que é o recorte do projeto (Linha 3-Vermelha). */
    public static final int NUMERO_LINHA_RECORTE = 3;

    private final LinhaDAO linhaDAO;
    private final LinhaEstacaoDAO linhaEstacaoDAO;

    public LinhaBO(LinhaDAO linhaDAO, LinhaEstacaoDAO linhaEstacaoDAO) {
        this.linhaDAO = linhaDAO;
        this.linhaEstacaoDAO = linhaEstacaoDAO;
    }

    public List<LinhaResponse> listar() {
        return linhaDAO.findAllByOrderByNumeroAsc().stream().map(LinhaResponse::de).toList();
    }

    public LinhaResponse buscar(Long id) {
        return LinhaResponse.de(buscarEntidade(id));
    }

    /** Estações da linha na ordem de percurso. */
    public List<EstacaoNaLinhaResponse> listarEstacoes(Long id) {
        buscarEntidade(id);
        return linhaEstacaoDAO.listarPorLinhaEmOrdem(id).stream().map(EstacaoNaLinhaResponse::de).toList();
    }

    @Transactional
    public LinhaResponse criar(LinhaRequest req) {
        if (linhaDAO.existsByNumero(req.numero())) {
            throw new RegraNegocioException("Já existe uma linha com o número " + req.numero() + ".");
        }
        Linha linha = new Linha(req.numero(), req.nome().trim(), req.cor().toUpperCase(), req.operadora());
        return LinhaResponse.de(linhaDAO.save(linha));
    }

    @Transactional
    public LinhaResponse atualizar(Long id, LinhaRequest req) {
        Linha linha = buscarEntidade(id);
        if (linhaDAO.existsByNumeroAndIdNot(req.numero(), id)) {
            throw new RegraNegocioException("Já existe outra linha com o número " + req.numero() + ".");
        }
        linha.setNumero(req.numero());
        linha.setNome(req.nome().trim());
        linha.setCor(req.cor().toUpperCase());
        linha.setOperadora(req.operadora());
        return LinhaResponse.de(linhaDAO.save(linha));
    }

    @Transactional
    public void excluir(Long id) {
        Linha linha = buscarEntidade(id);
        linhaDAO.delete(linha);
        linhaDAO.flush(); // força a checagem de FK aqui → DataIntegrityViolationException (409)
    }

    private Linha buscarEntidade(Long id) {
        return linhaDAO.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Linha com id " + id + " não encontrada."));
    }
}
