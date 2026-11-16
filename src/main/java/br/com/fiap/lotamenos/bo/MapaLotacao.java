package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.model.RelatoLotacao;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class MapaLotacao {
    private final LocalDate data;
    private final Map<Long, Map<String, Integer>> historicoPorEstacao;
    private final Map<Long, List<RelatoLotacao>> relatosPorEstacao;
    private final LocalDateTime agora;

    public MapaLotacao(LocalDate data,
                       Map<Long, Map<String, Integer>> historicoPorEstacao,
                       Map<Long, List<RelatoLotacao>> relatosPorEstacao,
                       LocalDateTime agora) {
        this.data = data;
        this.historicoPorEstacao = historicoPorEstacao;
        this.relatosPorEstacao = relatosPorEstacao;
        this.agora = agora;
    }

    public int indice(Long estacaoId, LocalTime horario) {
        LocalDateTime dataHora = data.atTime(horario);
        int base = historicoPorEstacao.getOrDefault(estacaoId, Map.of())
                .getOrDefault(HorarioUtil.faixaDe(horario), LotacaoBO.INDICE_SEM_HISTORICO);
        List<RelatoLotacao> relatos = LotacaoBO.consideraRelatos(dataHora, agora)
                ? relatosPorEstacao.getOrDefault(estacaoId, List.of())
                : List.of();
        return LotacaoBO.combinar(base, relatos, agora).indice();
    }
}
