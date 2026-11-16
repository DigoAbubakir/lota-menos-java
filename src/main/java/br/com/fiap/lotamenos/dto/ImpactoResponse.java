package br.com.fiap.lotamenos.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Indicador de impacto da CitizenTech LotaMenos")
public record ImpactoResponse(
        @Schema(example = "42") long totalConsultas,
        @Schema(example = "25") long totalSugestoesAceitas,
        @Schema(description = "% de sugestões aceitas sobre o total de consultas", example = "59.5")
        double percentualAceitas,
        @Schema(description = "Média de pontos de lotação evitados nas sugestões aceitas", example = "31.4")
        double reducaoMediaPontos,
        @Schema(example = "130") long totalRelatos,
        @Schema(description = "Usuários com relato ou consulta nos últimos 30 dias", example = "18")
        long usuariosAtivos
) {
}
