package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Resultado do planejamento: melhor horário de saída dentro da janela")
public record ViagemResponse(
        @Schema(example = "12") Long consultaId,
        @Schema(example = "Artur Alvim") String origem,
        @Schema(example = "Sé") String destino,
        @Schema(example = "SENTIDO_BARRA_FUNDA") Sentido sentido,
        @Schema(example = "07:00") String horarioDesejado,
        @Schema(example = "92") Integer indiceDesejado,
        @Schema(example = "LOTADO") NivelLotacao nivelDesejado,
        @Schema(example = "06:30") String horarioSugerido,
        @Schema(example = "58") Integer indiceSugerido,
        @Schema(example = "MODERADO") NivelLotacao nivelSugerido,
        @Schema(example = "34") Integer reducaoPontos,
        @Schema(example = "Brás") String estacaoCritica,
        @Schema(example = "false") boolean alertaAcessibilidade,
        @Schema(example = "Saindo às 06:30 em vez de 07:00 você evita o pico. Trecho mais cheio: Brás.")
        String dica,
        @Schema(example = "1") Long usuarioId,
        @Schema(example = "2026-11-17") LocalDate data,
        @Schema(description = "true = aceitou, false = recusou, null = não respondeu", example = "null")
        Boolean sugestaoAceita
) {
}
