package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.HistoricoLotacaoDAO;
import br.com.fiap.lotamenos.dao.RelatoLotacaoDAO;
import br.com.fiap.lotamenos.model.Estacao;
import br.com.fiap.lotamenos.model.HistoricoLotacao;
import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.RelatoLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LotacaoBO {
    public static final int INDICE_SEM_HISTORICO = 30;

    public static final int JANELA_RELATOS_MINUTOS = 30;

    public static final double PESO_MAXIMO_RELATOS = 0.6;

    public static final double FATOR_PESO_RELATOS = 0.15;

    private final HistoricoLotacaoDAO historicoDAO;
    private final RelatoLotacaoDAO relatoDAO;
    private final Clock clock;

    public LotacaoBO(HistoricoLotacaoDAO historicoDAO, RelatoLotacaoDAO relatoDAO, Clock clock) {
        this.historicoDAO = historicoDAO;
        this.relatoDAO = relatoDAO;
        this.clock = clock;
    }

    public int calcularIndice(Estacao estacao, Linha linha, Sentido sentido, LocalDateTime dataHora) {
        return calcularDetalhado(estacao, linha, sentido, dataHora).indice();
    }

    public ResultadoIndice calcularDetalhado(Estacao estacao, Linha linha, Sentido sentido, LocalDateTime dataHora) {
        LocalDateTime agora = LocalDateTime.now(clock);
        int base = historicoDAO.findByEstacaoIdAndLinhaIdAndSentidoAndTipoDiaAndFaixaHorario(
                        estacao.getId(), linha.getId(), sentido,
                        TipoDia.de(dataHora.toLocalDate()), HorarioUtil.faixaDe(dataHora.toLocalTime()))
                .map(HistoricoLotacao::getIndice)
                .orElse(INDICE_SEM_HISTORICO);

        List<RelatoLotacao> relatos = List.of();
        if (consideraRelatos(dataHora, agora)) {
            relatos = relatoDAO.buscarRecentes(estacao.getId(), linha.getId(), sentido,
                    agora.minusMinutes(JANELA_RELATOS_MINUTOS), agora);
        }
        return combinar(base, relatos, agora);
    }

    public MapaLotacao carregarMapa(Linha linha, Sentido sentido, LocalDate data) {
        LocalDateTime agora = LocalDateTime.now(clock);
        Map<Long, Map<String, Integer>> historico = new HashMap<>();
        for (HistoricoLotacao h : historicoDAO.findByLinhaIdAndSentidoAndTipoDia(
                linha.getId(), sentido, TipoDia.de(data))) {
            historico.computeIfAbsent(h.getEstacao().getId(), k -> new HashMap<>())
                    .put(h.getFaixaHorario(), h.getIndice());
        }
        Map<Long, List<RelatoLotacao>> relatos = relatoDAO.buscarRecentesDaLinha(linha.getId(), sentido,
                        agora.minusMinutes(JANELA_RELATOS_MINUTOS), agora).stream()
                .collect(Collectors.groupingBy(r -> r.getEstacao().getId()));
        return new MapaLotacao(data, historico, relatos, agora);
    }

    static boolean consideraRelatos(LocalDateTime dataHora, LocalDateTime agora) {
        long segundos = Math.abs(Duration.between(agora, dataHora).getSeconds());
        return segundos <= JANELA_RELATOS_MINUTOS * 60L;
    }

    public static ResultadoIndice combinar(int base, List<RelatoLotacao> relatos, LocalDateTime agora) {
        double somaPesos = 0;
        double somaPonderada = 0;
        int considerados = 0;
        for (RelatoLotacao relato : relatos) {
            double minutos = Math.max(0, Duration.between(relato.getDataHora(), agora).getSeconds() / 60.0);
            double decaimento = Math.max(0, 1 - minutos / JANELA_RELATOS_MINUTOS);
            double peso = relato.getUsuario().getReputacao() * decaimento;
            if (peso <= 0) {
                continue;
            }
            somaPesos += peso;
            somaPonderada += peso * relato.getNivel().getValor();
            considerados++;
        }
        if (somaPesos == 0) {
            return new ResultadoIndice(limitar(base), base, 0, 0);
        }
        double mediaRelatos = somaPonderada / somaPesos;
        double w = Math.min(PESO_MAXIMO_RELATOS, FATOR_PESO_RELATOS * somaPesos);
        int indice = (int) Math.round(base * (1 - w) + mediaRelatos * w);
        return new ResultadoIndice(limitar(indice), base, considerados, Math.round(w * 100) / 100.0);
    }

    private static int limitar(int indice) {
        return Math.max(0, Math.min(100, indice));
    }
}
