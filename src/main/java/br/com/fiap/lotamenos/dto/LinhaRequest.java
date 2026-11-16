package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.Operadora;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar/atualizar uma linha")
public record LinhaRequest(
        @NotNull(message = "Informe o número da linha.")
        @Min(value = 1, message = "O número da linha deve ser no mínimo 1.")
        @Max(value = 99, message = "O número da linha deve ser no máximo 99.")
        @Schema(example = "15")
        Integer numero,

        @NotBlank(message = "Informe o nome da linha.")
        @Size(max = 40, message = "O nome da linha deve ter no máximo 40 caracteres.")
        @Schema(example = "Prata")
        String nome,

        @NotBlank(message = "Informe a cor da linha.")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "A cor deve estar no formato hexadecimal #RRGGBB.")
        @Schema(example = "#A9A9A9")
        String cor,

        @NotNull(message = "Informe a operadora (METRO ou CPTM).")
        @Schema(example = "METRO")
        Operadora operadora
) {
}
