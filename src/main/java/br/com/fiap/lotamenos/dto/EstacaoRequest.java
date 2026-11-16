package br.com.fiap.lotamenos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar/atualizar uma estação")
public record EstacaoRequest(
        @NotBlank(message = "Informe o nome da estação.")
        @Size(max = 60, message = "O nome da estação deve ter no máximo 60 caracteres.")
        @Schema(example = "Luz")
        String nome,

        @NotNull(message = "Informe se a estação possui integração.")
        @Schema(example = "true")
        Boolean possuiIntegracao,

        @NotNull(message = "Informe se a estação está ativa.")
        @Schema(example = "true")
        Boolean ativa
) {
}
