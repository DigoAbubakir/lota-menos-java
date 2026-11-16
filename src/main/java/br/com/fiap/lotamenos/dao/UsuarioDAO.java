package br.com.fiap.lotamenos.dao;

import br.com.fiap.lotamenos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acesso a dados da tabela T_LM_USUARIO.
 */
public interface UsuarioDAO extends JpaRepository<Usuario, Long> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    List<Usuario> findAllByOrderByIdAsc();
}
