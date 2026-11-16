package br.com.fiap.lotamenos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Resposta do usuário à sugestão de horário")
public record AceiteRequest(
        @NotNull(message = "Informe se a sugestão foi aceita (true ou false).")
        @Schema(example = "true")
        Boolean aceita
) {
}
