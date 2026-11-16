package br.com.fiap.lotamenos.dao;

import br.com.fiap.lotamenos.model.RelatoLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Acesso a dados da tabela T_LM_RELATO_LOTACAO.
 */
public interface RelatoLotacaoDAO extends JpaRepository<RelatoLotacao, Long> {

    /** Relatos de uma estação/linha/sentido num intervalo (já com o usuário, para usar a reputação). */
    @Query("""
            select r from RelatoLotacao r join fetch r.usuario
            where r.estacao.id = :estacaoId and r.linha.id = :linhaId and r.sentido = :sentido
              and r.dataHora between :inicio and :fim
            """)
    List<RelatoLotacao> buscarRecentes(@Param("estacaoId") Long estacaoId,
                                       @Param("linhaId") Long linhaId,
                                       @Param("sentido") Sentido sentido,
                                       @Param("inicio") LocalDateTime inicio,
                                       @Param("fim") LocalDateTime fim);

    /** Relatos de todas as estações de uma linha/sentido num intervalo (uma consulta só). */
    @Query("""
            select r from RelatoLotacao r join fetch r.usuario join fetch r.estacao
            where r.linha.id = :linhaId and r.sentido = :sentido
              and r.dataHora between :inicio and :fim
            """)
    List<RelatoLotacao> buscarRecentesDaLinha(@Param("linhaId") Long linhaId,
                                              @Param("sentido") Sentido sentido,
                                              @Param("inicio") LocalDateTime inicio,
                                              @Param("fim") LocalDateTime fim);

    /** Antispam: existe relato do usuário na estação estritamente dentro do intervalo? */
    @Query("""
            select count(r) > 0 from RelatoLotacao r
            where r.usuario.id = :usuarioId and r.estacao.id = :estacaoId
              and r.dataHora > :inicio and r.dataHora < :fim
            """)
    boolean existeRelatoNoIntervalo(@Param("usuarioId") Long usuarioId,
                                    @Param("estacaoId") Long estacaoId,
                                    @Param("inicio") LocalDateTime inicio,
                                    @Param("fim") LocalDateTime fim);

    /** Listagem com filtro opcional de estação e data mínima. */
    @Query("""
            select r from RelatoLotacao r join fetch r.usuario join fetch r.estacao join fetch r.linha
            where (:estacaoId is null or r.estacao.id = :estacaoId)
              and r.dataHora >= :desde
            order by r.dataHora desc
            """)
    List<RelatoLotacao> filtrar(@Param("estacaoId") Long estacaoId, @Param("desde") LocalDateTime desde);

    @Query("select distinct r.usuario.id from RelatoLotacao r where r.dataHora >= :desde")
    List<Long> usuariosComRelatoDesde(@Param("desde") LocalDateTime desde);
}
