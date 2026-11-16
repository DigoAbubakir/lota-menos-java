package br.com.fiap.lotamenos.dao;

import br.com.fiap.lotamenos.model.Linha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinhaDAO extends JpaRepository<Linha, Long> {
    Optional<Linha> findByNumero(Integer numero);

    boolean existsByNumero(Integer numero);

    boolean existsByNumeroAndIdNot(Integer numero, Long id);

    List<Linha> findAllByOrderByNumeroAsc();
}
