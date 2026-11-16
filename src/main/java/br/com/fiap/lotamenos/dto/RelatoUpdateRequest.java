package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Correção de um relato (altera só nível e sentido; não recalcula reputação)")
public record RelatoUpdateRequest(
        @NotNull(message = "Informe o sentido (SENTIDO_BARRA_FUNDA ou SENTIDO_ITAQUERA).")
        @Schema(example = "SENTIDO_BARRA_FUNDA")
        Sentido sentido,

        @NotNull(message = "Informe o nível de lotação (VAZIO, MODERADO, CHEIO ou LOTADO).")
        @Schema(example = "CHEIO")
        NivelLotacao nivel
) {
}
