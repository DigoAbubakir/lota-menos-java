package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.Estacao;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estação")
public record EstacaoResponse(
        @Schema(example = "6") Long id,
        @Schema(example = "Sé") String nome,
        @Schema(example = "true") Boolean possuiIntegracao,
        @Schema(example = "true") Boolean ativa
) {
    public static EstacaoResponse de(Estacao e) {
        return new EstacaoResponse(e.getId(), e.getNome(), e.getPossuiIntegracao(), e.getAtiva());
    }
}
