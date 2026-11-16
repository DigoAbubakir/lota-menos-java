package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Índice LotaMenos de uma estação agora")
public record LotacaoAtualResponse(
        @Schema(example = "8") Long estacaoId,
        @Schema(example = "Brás") String estacao,
        @Schema(example = "SENTIDO_BARRA_FUNDA") Sentido sentido,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(example = "2026-11-17T07:05:00") LocalDateTime dataHora,
        @Schema(example = "07:00") String faixaHorario,
        @Schema(description = "Índice histórico da faixa", example = "92") int baseHistorica,
        @Schema(description = "Quantidade de relatos dos últimos 30 min considerados", example = "3")
        int relatosConsiderados,
        @Schema(description = "Peso dos relatos no cálculo (0 a 0.6)", example = "0.42") double pesoRelatos,
        @Schema(example = "94") int indice,
        @Schema(example = "LOTADO") NivelLotacao nivel
) {
}
