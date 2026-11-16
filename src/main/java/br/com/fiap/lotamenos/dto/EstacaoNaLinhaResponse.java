package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.LinhaEstacao;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Estação dentro de uma linha, com a ordem de percurso")
public record EstacaoNaLinhaResponse(
        @Schema(example = "6") Integer ordem,
        @Schema(example = "6") Long estacaoId,
        @Schema(example = "Sé") String nome,
        @Schema(example = "true") Boolean possuiIntegracao,
        @Schema(example = "true") Boolean ativa
) {
    public static EstacaoNaLinhaResponse de(LinhaEstacao le) {
        return new EstacaoNaLinhaResponse(le.getOrdem(), le.getEstacao().getId(), le.getEstacao().getNome(),
                le.getEstacao().getPossuiIntegracao(), le.getEstacao().getAtiva());
    }
}
