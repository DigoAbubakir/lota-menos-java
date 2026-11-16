package br.com.fiap.lotamenos.dao;

import br.com.fiap.lotamenos.model.RelatoLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RelatoLotacaoDAO extends JpaRepository<RelatoLotacao, Long> {
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

    @Query("""
            select r from RelatoLotacao r join fetch r.usuario join fetch r.estacao
            where r.linha.id = :linhaId and r.sentido = :sentido
              and r.dataHora between :inicio and :fim
            """)
    List<RelatoLotacao> buscarRecentesDaLinha(@Param("linhaId") Long linhaId,
                                              @Param("sentido") Sentido sentido,
                                              @Param("inicio") LocalDateTime inicio,
                                              @Param("fim") LocalDateTime fim);

    @Query("""
            select count(r) > 0 from RelatoLotacao r
            where r.usuario.id = :usuarioId and r.estacao.id = :estacaoId
              and r.dataHora > :inicio and r.dataHora < :fim
            """)
    boolean existeRelatoNoIntervalo(@Param("usuarioId") Long usuarioId,
                                    @Param("estacaoId") Long estacaoId,
                                    @Param("inicio") LocalDateTime inicio,
                                    @Param("fim") LocalDateTime fim);

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
