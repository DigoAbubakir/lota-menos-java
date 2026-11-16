package br.com.fiap.lotamenos.dao;

import br.com.fiap.lotamenos.model.ConsultaViagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Acesso a dados da tabela T_LM_CONSULTA_VIAGEM.
 */
public interface ConsultaViagemDAO extends JpaRepository<ConsultaViagem, Long> {

    @Query("""
            select c from ConsultaViagem c
            join fetch c.usuario join fetch c.origem join fetch c.destino left join fetch c.estacaoCritica
            order by c.dataConsulta desc
            """)
    List<ConsultaViagem> listarTodas();

    long countBySugestaoAceitaTrue();

    /** Média de (índice desejado − índice sugerido) nas sugestões aceitas. */
    @Query("select avg(c.indiceDesejado - c.indiceSugerido) from ConsultaViagem c where c.sugestaoAceita = true")
    Double mediaReducaoNasAceitas();

    @Query("select distinct c.usuario.id from ConsultaViagem c where c.dataConsulta >= :desde")
    List<Long> usuariosComConsultaDesde(@Param("desde") LocalDateTime desde);
}
