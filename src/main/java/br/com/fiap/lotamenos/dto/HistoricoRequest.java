package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Registro de histórico de lotação (faixa de 15 minutos)")
public record HistoricoRequest(
        @NotNull(message = "Informe a estação.")
        @Schema(example = "8")
        Long estacaoId,

        @NotNull(message = "Informe a linha.")
        @Schema(example = "2")
        Long linhaId,

        @NotNull(message = "Informe o sentido (SENTIDO_BARRA_FUNDA ou SENTIDO_ITAQUERA).")
        @Schema(example = "SENTIDO_BARRA_FUNDA")
        Sentido sentido,

        @NotNull(message = "Informe o tipo de dia (UTIL, SABADO ou DOMINGO).")
        @Schema(example = "UTIL")
        TipoDia tipoDia,

        @NotBlank(message = "Informe a faixa de horário.")
        @Pattern(regexp = "^([01]\\d|2[0-3]):(00|15|30|45)$",
                message = "A faixa deve estar no formato HH:mm e em múltiplos de 15 minutos (ex.: 07:15).")
        @Schema(example = "04:30")
        String faixaHorario,

        @NotNull(message = "Informe o índice.")
        @Min(value = 0, message = "O índice deve estar entre 0 e 100.")
        @Max(value = 100, message = "O índice deve estar entre 0 e 100.")
        @Schema(example = "15")
        Integer indice
) {
}
