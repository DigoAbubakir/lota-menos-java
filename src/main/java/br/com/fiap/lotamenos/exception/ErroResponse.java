package br.com.fiap.lotamenos.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta padrão de erro")
public record ErroResponse(
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(example = "2026-11-17T07:02:11")
        LocalDateTime timestamp,
        @Schema(example = "422")
        int status,
        @Schema(example = "Regra de negócio")
        String erro,
        @Schema(example = "A estação Luz não faz parte do recorte da Linha 3.")
        String mensagem,
        @Schema(example = "/viagens/planejar")
        String caminho,
        List<CampoInvalido> campos
) {
    public record CampoInvalido(
            @Schema(example = "email") String campo,
            @Schema(example = "Informe um e-mail válido.") String mensagem) {
    }
}
