package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.PerfilUsuario;
import br.com.fiap.lotamenos.model.Usuario;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Usuário")
public record UsuarioResponse(
        @Schema(example = "1") Long id,
        @Schema(example = "Carlos Mendes") String nome,
        @Schema(example = "carlos.mendes@exemplo.com") String email,
        @Schema(example = "TRABALHADOR") PerfilUsuario perfil,
        @Schema(example = "1.05") Double reputacao,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        @Schema(example = "2026-11-10T08:00:00") LocalDateTime dataCadastro
) {
    public static UsuarioResponse de(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(),
                u.getReputacao(), u.getDataCadastro());
    }
}
