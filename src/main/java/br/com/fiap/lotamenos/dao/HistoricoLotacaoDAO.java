package br.com.fiap.lotamenos.dao;

import br.com.fiap.lotamenos.model.HistoricoLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoricoLotacaoDAO extends JpaRepository<HistoricoLotacao, Long> {
    Optional<HistoricoLotacao> findByEstacaoIdAndLinhaIdAndSentidoAndTipoDiaAndFaixaHorario(
            Long estacaoId, Long linhaId, Sentido sentido, TipoDia tipoDia, String faixaHorario);

    boolean existsByEstacaoIdAndLinhaIdAndSentidoAndTipoDiaAndFaixaHorario(
            Long estacaoId, Long linhaId, Sentido sentido, TipoDia tipoDia, String faixaHorario);

    List<HistoricoLotacao> findByLinhaIdAndSentidoAndTipoDia(Long linhaId, Sentido sentido, TipoDia tipoDia);

    @Query("""
            select h from HistoricoLotacao h join fetch h.estacao join fetch h.linha
            where (:estacaoId is null or h.estacao.id = :estacaoId)
              and (:sentido is null or h.sentido = :sentido)
              and (:tipoDia is null or h.tipoDia = :tipoDia)
            order by h.estacao.id, h.sentido, h.tipoDia, h.faixaHorario
            """)
    List<HistoricoLotacao> filtrar(@Param("estacaoId") Long estacaoId,
                                   @Param("sentido") Sentido sentido,
                                   @Param("tipoDia") TipoDia tipoDia);
}
