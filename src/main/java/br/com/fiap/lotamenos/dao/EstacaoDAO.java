package br.com.fiap.lotamenos.dao;

import br.com.fiap.lotamenos.model.Estacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstacaoDAO extends JpaRepository<Estacao, Long> {
    Optional<Estacao> findByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCase(String nome);

    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    List<Estacao> findAllByOrderByNomeAsc();
}
