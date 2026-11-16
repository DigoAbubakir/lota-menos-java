package br.com.fiap.lotamenos.dto;

import br.com.fiap.lotamenos.model.PerfilUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para criar/atualizar um usuário (use dados fictícios)")
public record UsuarioRequest(
        @NotBlank(message = "Informe o nome.")
        @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
        @Schema(example = "Fernanda Teste")
        String nome,

        @NotBlank(message = "Informe o e-mail.")
        @Email(message = "Informe um e-mail válido.")
        @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres.")
        @Schema(example = "fernanda.teste@exemplo.com")
        String email,

        @NotNull(message = "Informe o perfil (TRABALHADOR, IDOSO, MOBILIDADE_REDUZIDA ou ESTUDANTE).")
        @Schema(example = "TRABALHADOR")
        PerfilUsuario perfil
) {
}
