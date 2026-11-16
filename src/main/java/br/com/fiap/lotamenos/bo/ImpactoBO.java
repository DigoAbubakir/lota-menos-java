package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.ConsultaViagemDAO;
import br.com.fiap.lotamenos.dao.RelatoLotacaoDAO;
import br.com.fiap.lotamenos.dto.ImpactoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ImpactoBO {
    public static final int DIAS_USUARIO_ATIVO = 30;

    private final ConsultaViagemDAO consultaDAO;
    private final RelatoLotacaoDAO relatoDAO;
    private final Clock clock;

    public ImpactoBO(ConsultaViagemDAO consultaDAO, RelatoLotacaoDAO relatoDAO, Clock clock) {
        this.consultaDAO = consultaDAO;
        this.relatoDAO = relatoDAO;
        this.clock = clock;
    }

    public ImpactoResponse calcular() {
        long totalConsultas = consultaDAO.count();
        long aceitas = consultaDAO.countBySugestaoAceitaTrue();
        double percentual = totalConsultas == 0 ? 0 : arredondar(aceitas * 100.0 / totalConsultas);
        Double media = consultaDAO.mediaReducaoNasAceitas();
        double reducaoMedia = media == null ? 0 : arredondar(media);

        LocalDateTime desde = LocalDateTime.now(clock).minusDays(DIAS_USUARIO_ATIVO);
        Set<Long> ativos = new HashSet<>(relatoDAO.usuariosComRelatoDesde(desde));
        ativos.addAll(consultaDAO.usuariosComConsultaDesde(desde));

        return new ImpactoResponse(totalConsultas, aceitas, percentual, reducaoMedia, relatoDAO.count(), ativos.size());
    }

    private static double arredondar(double valor) {
        return Math.round(valor * 10) / 10.0;
    }
}
