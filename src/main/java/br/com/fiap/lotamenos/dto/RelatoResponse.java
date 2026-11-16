package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.RelatoLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Relato de lotação")
public record RelatoResponse(
        @Schema(example = "15") Long id,
        @Schema(example = "1") Long usuarioId,
        @Schema(example = "Carlos Mendes") String usuario,
        @Schema(description = "Reputação do usuário após o relato", example = "1.05") Double reputacaoUsuario,
        @Schema(example = "8") Long estacaoId,
        @Schema(example = "Brás") String estacao,
        @Schema(example = "2") Long linhaId,
        @Schema(example = "3") Integer numeroLinha,
        @Schema(example = "SENTIDO_BARRA_FUNDA") Sentido sentido,
        @Schema(example = "LOTADO") NivelLotacao nivel,
        @Schema(example = "95") int valorNivel,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(example = "2026-11-17T07:05:00") LocalDateTime dataHora
) {
    public static RelatoResponse de(RelatoLotacao r) {
        return new RelatoResponse(r.getId(), r.getUsuario().getId(), r.getUsuario().getNome(),
                r.getUsuario().getReputacao(), r.getEstacao().getId(), r.getEstacao().getNome(),
                r.getLinha().getId(), r.getLinha().getNumero(), r.getSentido(), r.getNivel(),
                r.getNivel().getValor(), r.getDataHora());
    }
}
