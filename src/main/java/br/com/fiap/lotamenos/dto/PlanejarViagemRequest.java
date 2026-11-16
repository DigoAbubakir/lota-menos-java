package br.com.fiap.lotamenos.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Schema(description = "Pedido de planejamento: origem, destino e janela em que o usuário pode sair")
public record PlanejarViagemRequest(
        @NotNull(message = "Informe o usuário.")
        @Schema(example = "1")
        Long usuarioId,

        @NotNull(message = "Informe a estação de origem.")
        @Schema(description = "Id da estação de origem (Linha 3)", example = "17")
        Long origemId,

        @NotNull(message = "Informe a estação de destino.")
        @Schema(description = "Id da estação de destino (Linha 3)", example = "6")
        Long destinoId,

        @NotBlank(message = "Informe o início da janela.")
        @Pattern(regexp = HORA, message = "O início da janela deve estar no formato HH:mm.")
        @Schema(example = "06:00")
        String janelaInicio,

        @NotBlank(message = "Informe o fim da janela.")
        @Pattern(regexp = HORA, message = "O fim da janela deve estar no formato HH:mm.")
        @Schema(example = "08:00")
        String janelaFim,

        @Pattern(regexp = HORA, message = "O horário desejado deve estar no formato HH:mm.")
        @Schema(description = "Opcional; padrão = início da janela", example = "07:00")
        String horarioDesejado,

        @Schema(description = "Opcional; padrão = hoje", example = "2026-11-17")
        LocalDate data
) {
    public static final String HORA = "^([01]\\d|2[0-3]):[0-5]\\d$";
}
