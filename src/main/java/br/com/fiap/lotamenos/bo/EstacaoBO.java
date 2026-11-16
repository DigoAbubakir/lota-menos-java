package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.EstacaoDAO;
import br.com.fiap.lotamenos.dao.LinhaDAO;
import br.com.fiap.lotamenos.dao.LinhaEstacaoDAO;
import br.com.fiap.lotamenos.dto.EstacaoRequest;
import br.com.fiap.lotamenos.dto.EstacaoResponse;
import br.com.fiap.lotamenos.dto.LotacaoAtualResponse;
import br.com.fiap.lotamenos.exception.EstacaoForaDoRecorteException;
import br.com.fiap.lotamenos.exception.RecursoNaoEncontradoException;
import br.com.fiap.lotamenos.exception.RegraNegocioException;
import br.com.fiap.lotamenos.model.Estacao;
import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Regras das estações.
 * <ul>
 *   <li>O nome da estação é único (sem diferenciar maiúsculas/minúsculas).</li>
 *   <li>Lotação atual: só para estações da Linha 3 (recorte); usa o Índice LotaMenos de agora
 *       ({@link LotacaoBO}), combinando histórico e relatos dos últimos 30 min.</li>
 *   <li>Excluir uma estação com relatos, histórico ou consultas vinculados gera conflito (409).</li>
 * </ul>
 */
@Service
@Transactional(readOnly = true)
public class EstacaoBO {

    private final EstacaoDAO estacaoDAO;
    private final LinhaDAO linhaDAO;
    private final LinhaEstacaoDAO linhaEstacaoDAO;
    private final LotacaoBO lotacaoBO;
    private final Clock clock;

    public EstacaoBO(EstacaoDAO estacaoDAO, LinhaDAO linhaDAO, LinhaEstacaoDAO linhaEstacaoDAO,
                     LotacaoBO lotacaoBO, Clock clock) {
        this.estacaoDAO = estacaoDAO;
        this.linhaDAO = linhaDAO;
        this.linhaEstacaoDAO = linhaEstacaoDAO;
        this.lotacaoBO = lotacaoBO;
        this.clock = clock;
    }

    public List<EstacaoResponse> listar() {
        return estacaoDAO.findAllByOrderByNomeAsc().stream().map(EstacaoResponse::de).toList();
    }

    public EstacaoResponse buscar(Long id) {
        return EstacaoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public EstacaoResponse criar(EstacaoRequest req) {
        String nome = req.nome().trim();
        if (estacaoDAO.existsByNomeIgnoreCase(nome)) {
            throw new RegraNegocioException("Já existe uma estação com o nome " + nome + ".");
        }
        return EstacaoResponse.de(estacaoDAO.save(new Estacao(nome, req.possuiIntegracao(), req.ativa())));
    }

    @Transactional
    public EstacaoResponse atualizar(Long id, EstacaoRequest req) {
        Estacao estacao = buscarEntidade(id);
        String nome = req.nome().trim();
        if (estacaoDAO.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new RegraNegocioException("Já existe outra estação com o nome " + nome + ".");
        }
        estacao.setNome(nome);
        estacao.setPossuiIntegracao(req.possuiIntegracao());
        estacao.setAtiva(req.ativa());
        return EstacaoResponse.de(estacaoDAO.save(estacao));
    }

    @Transactional
    public void excluir(Long id) {
        Estacao estacao = buscarEntidade(id);
        estacaoDAO.delete(estacao);
        estacaoDAO.flush(); // FK violada → DataIntegrityViolationException (409)
    }

    /** Índice LotaMenos e nível da estação agora, no sentido informado. */
    public LotacaoAtualResponse lotacaoAtual(Long id, Sentido sentido) {
        Estacao estacao = buscarEntidade(id);
        Linha linha3 = linhaDAO.findByNumero(LinhaBO.NUMERO_LINHA_RECORTE)
                .orElseThrow(() -> new RecursoNaoEncontradoException("A Linha 3-Vermelha não está cadastrada."));
        if (!linhaEstacaoDAO.existsByLinhaIdAndEstacaoId(linha3.getId(), estacao.getId())) {
            throw EstacaoForaDoRecorteException.daEstacao(estacao.getNome());
        }
        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);
        ResultadoIndice r = lotacaoBO.calcularDetalhado(estacao, linha3, sentido, agora);
        return new LotacaoAtualResponse(estacao.getId(), estacao.getNome(), sentido, agora,
                HorarioUtil.faixaDe(agora.toLocalTime()), r.base(), r.relatosConsiderados(), r.pesoRelatos(),
                r.indice(), NivelLotacao.deIndice(r.indice()));
    }

    private Estacao buscarEntidade(Long id) {
        return estacaoDAO.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estação com id " + id + " não encontrada."));
    }
}
