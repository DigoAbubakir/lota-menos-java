package br.com.fiap.lotamenos.dao;

import br.com.fiap.lotamenos.model.LinhaEstacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Acesso a dados da tabela T_LM_LINHA_ESTACAO.
 */
public interface LinhaEstacaoDAO extends JpaRepository<LinhaEstacao, Long> {

    /** Estações de uma linha em ordem de percurso (já trazendo a estação). */
    @Query("select le from LinhaEstacao le join fetch le.estacao where le.linha.id = :linhaId order by le.ordem")
    List<LinhaEstacao> listarPorLinhaEmOrdem(@Param("linhaId") Long linhaId);

    Optional<LinhaEstacao> findByLinhaIdAndEstacaoId(Long linhaId, Long estacaoId);

    boolean existsByLinhaIdAndEstacaoId(Long linhaId, Long estacaoId);
}
