package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.EstacaoDAO;
import br.com.fiap.lotamenos.dao.HistoricoLotacaoDAO;
import br.com.fiap.lotamenos.dao.LinhaDAO;
import br.com.fiap.lotamenos.dao.LinhaEstacaoDAO;
import br.com.fiap.lotamenos.dto.HistoricoRequest;
import br.com.fiap.lotamenos.dto.HistoricoResponse;
import br.com.fiap.lotamenos.exception.EstacaoForaDoRecorteException;
import br.com.fiap.lotamenos.exception.RecursoNaoEncontradoException;
import br.com.fiap.lotamenos.exception.RegraNegocioException;
import br.com.fiap.lotamenos.model.Estacao;
import br.com.fiap.lotamenos.model.HistoricoLotacao;
import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class HistoricoLotacaoBO {
    private final HistoricoLotacaoDAO historicoDAO;
    private final EstacaoDAO estacaoDAO;
    private final LinhaDAO linhaDAO;
    private final LinhaEstacaoDAO linhaEstacaoDAO;

    public HistoricoLotacaoBO(HistoricoLotacaoDAO historicoDAO, EstacaoDAO estacaoDAO, LinhaDAO linhaDAO,
                              LinhaEstacaoDAO linhaEstacaoDAO) {
        this.historicoDAO = historicoDAO;
        this.estacaoDAO = estacaoDAO;
        this.linhaDAO = linhaDAO;
        this.linhaEstacaoDAO = linhaEstacaoDAO;
    }

    public List<HistoricoResponse> listar(Long estacaoId, Sentido sentido, TipoDia tipoDia) {
        return historicoDAO.filtrar(estacaoId, sentido, tipoDia).stream().map(HistoricoResponse::de).toList();
    }

    public HistoricoResponse buscar(Long id) {
        return HistoricoResponse.de(buscarEntidade(id));
    }

    @Transactional
    public HistoricoResponse criar(HistoricoRequest req) {
        Estacao estacao = buscarEstacao(req.estacaoId());
        Linha linha = buscarLinha(req.linhaId());
        validarEstacaoNaLinha(estacao, linha);
        if (historicoDAO.existsByEstacaoIdAndLinhaIdAndSentidoAndTipoDiaAndFaixaHorario(
                estacao.getId(), linha.getId(), req.sentido(), req.tipoDia(), req.faixaHorario())) {
            throw new RegraNegocioException("Já existe histórico para " + estacao.getNome() + ", "
                    + req.sentido() + ", " + req.tipoDia() + ", faixa " + req.faixaHorario() + ".");
        }
        HistoricoLotacao h = new HistoricoLotacao(estacao, linha, req.sentido(), req.tipoDia(),
                req.faixaHorario(), req.indice());
        return HistoricoResponse.de(historicoDAO.save(h));
    }

    @Transactional
    public HistoricoResponse atualizar(Long id, HistoricoRequest req) {
        HistoricoLotacao h = buscarEntidade(id);
        Estacao estacao = buscarEstacao(req.estacaoId());
        Linha linha = buscarLinha(req.linhaId());
        validarEstacaoNaLinha(estacao, linha);
        historicoDAO.findByEstacaoIdAndLinhaIdAndSentidoAndTipoDiaAndFaixaHorario(
                        estacao.getId(), linha.getId(), req.sentido(), req.tipoDia(), req.faixaHorario())
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new RegraNegocioException("Já existe outro histórico (id " + outro.getId()
                            + ") para essa estação, linha, sentido, tipo de dia e faixa.");
                });
        h.setEstacao(estacao);
        h.setLinha(linha);
        h.setSentido(req.sentido());
        h.setTipoDia(req.tipoDia());
        h.setFaixaHorario(req.faixaHorario());
        h.setIndice(req.indice());
        return HistoricoResponse.de(historicoDAO.save(h));
    }

    @Transactional
    public void excluir(Long id) {
        historicoDAO.delete(buscarEntidade(id));
    }

    private void validarEstacaoNaLinha(Estacao estacao, Linha linha) {
        if (!linhaEstacaoDAO.existsByLinhaIdAndEstacaoId(linha.getId(), estacao.getId())) {
            throw new EstacaoForaDoRecorteException("A estação " + estacao.getNome()
                    + " não pertence à Linha " + linha.getNumero() + "-" + linha.getNome() + ".");
        }
    }

    private HistoricoLotacao buscarEntidade(Long id) {
        return historicoDAO.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Histórico com id " + id + " não encontrado."));
    }

    private Estacao buscarEstacao(Long id) {
        return estacaoDAO.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Estação com id " + id + " não encontrada."));
    }

    private Linha buscarLinha(Long id) {
        return linhaDAO.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Linha com id " + id + " não encontrada."));
    }
}
