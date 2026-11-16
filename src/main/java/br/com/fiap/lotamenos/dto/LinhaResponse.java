package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.Operadora;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Linha de metrô/trem")
public record LinhaResponse(
        @Schema(example = "2") Long id,
        @Schema(example = "3") Integer numero,
        @Schema(example = "Vermelha") String nome,
        @Schema(example = "#EE372F") String cor,
        @Schema(example = "METRO") Operadora operadora
) {
    public static LinhaResponse de(Linha l) {
        return new LinhaResponse(l.getId(), l.getNumero(), l.getNome(), l.getCor(), l.getOperadora());
    }
}
