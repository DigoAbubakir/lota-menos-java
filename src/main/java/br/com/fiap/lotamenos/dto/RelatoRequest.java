package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Relato de lotação em tempo real")
public record RelatoRequest(
        @NotNull(message = "Informe o usuário.")
        @Schema(example = "1")
        Long usuarioId,

        @NotNull(message = "Informe a estação.")
        @Schema(example = "8")
        Long estacaoId,

        @Schema(description = "Linha do relato (opcional; padrão = Linha 3-Vermelha, único recorte aceito)",
                example = "2")
        Long linhaId,

        @NotNull(message = "Informe o sentido (SENTIDO_BARRA_FUNDA ou SENTIDO_ITAQUERA).")
        @Schema(example = "SENTIDO_BARRA_FUNDA")
        Sentido sentido,

        @NotNull(message = "Informe o nível de lotação (VAZIO, MODERADO, CHEIO ou LOTADO).")
        @Schema(example = "LOTADO")
        NivelLotacao nivel,

        @Schema(description = "Momento do relato (opcional; padrão = agora; não pode ser no futuro)",
                example = "2026-11-17T07:05:00")
        LocalDateTime dataHora
) {
}
