package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.HistoricoLotacao;
import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Histórico de lotação")
public record HistoricoResponse(
        @Schema(example = "1234") Long id,
        @Schema(example = "8") Long estacaoId,
        @Schema(example = "Brás") String estacao,
        @Schema(example = "2") Long linhaId,
        @Schema(example = "3") Integer numeroLinha,
        @Schema(example = "SENTIDO_BARRA_FUNDA") Sentido sentido,
        @Schema(example = "UTIL") TipoDia tipoDia,
        @Schema(example = "07:30") String faixaHorario,
        @Schema(example = "96") Integer indice,
        @Schema(example = "LOTADO") NivelLotacao nivel
) {
    public static HistoricoResponse de(HistoricoLotacao h) {
        return new HistoricoResponse(h.getId(), h.getEstacao().getId(), h.getEstacao().getNome(),
                h.getLinha().getId(), h.getLinha().getNumero(), h.getSentido(), h.getTipoDia(),
                h.getFaixaHorario(), h.getIndice(), NivelLotacao.deIndice(h.getIndice()));
    }
}
