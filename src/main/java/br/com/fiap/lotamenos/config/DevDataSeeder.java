package br.com.fiap.lotamenos.config;

import br.com.fiap.lotamenos.dao.ConsultaViagemDAO;
import br.com.fiap.lotamenos.dao.EstacaoDAO;
import br.com.fiap.lotamenos.dao.HistoricoLotacaoDAO;
import br.com.fiap.lotamenos.dao.LinhaDAO;
import br.com.fiap.lotamenos.dao.LinhaEstacaoDAO;
import br.com.fiap.lotamenos.dao.RelatoLotacaoDAO;
import br.com.fiap.lotamenos.dao.UsuarioDAO;
import br.com.fiap.lotamenos.model.ConsultaViagem;
import br.com.fiap.lotamenos.model.Estacao;
import br.com.fiap.lotamenos.model.HistoricoLotacao;
import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.LinhaEstacao;
import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Operadora;
import br.com.fiap.lotamenos.model.PerfilUsuario;
import br.com.fiap.lotamenos.model.RelatoLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;
import br.com.fiap.lotamenos.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static br.com.fiap.lotamenos.model.Sentido.SENTIDO_BARRA_FUNDA;
import static br.com.fiap.lotamenos.model.Sentido.SENTIDO_ITAQUERA;

@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    public static final List<String> ESTACOES_LINHA_3 = List.of(
            "Palmeiras-Barra Funda", "Marechal Deodoro", "Santa Cecília", "República", "Anhangabaú", "Sé",
            "Pedro II", "Brás", "Bresser-Mooca", "Belém", "Tatuapé", "Carrão", "Penha", "Vila Matilde",
            "Guilhermina-Esperança", "Patriarca", "Artur Alvim", "Corinthians-Itaquera");

    private static final Set<String> COM_INTEGRACAO =
            Set.of("Palmeiras-Barra Funda", "Sé", "Brás", "Tatuapé", "Corinthians-Itaquera");

    private final LinhaDAO linhaDAO;
    private final EstacaoDAO estacaoDAO;
    private final LinhaEstacaoDAO linhaEstacaoDAO;
    private final HistoricoLotacaoDAO historicoDAO;
    private final UsuarioDAO usuarioDAO;
    private final RelatoLotacaoDAO relatoDAO;
    private final ConsultaViagemDAO consultaDAO;
    private final Clock clock;

    public DevDataSeeder(LinhaDAO linhaDAO, EstacaoDAO estacaoDAO, LinhaEstacaoDAO linhaEstacaoDAO,
                         HistoricoLotacaoDAO historicoDAO, UsuarioDAO usuarioDAO, RelatoLotacaoDAO relatoDAO,
                         ConsultaViagemDAO consultaDAO, Clock clock) {
        this.linhaDAO = linhaDAO;
        this.estacaoDAO = estacaoDAO;
        this.linhaEstacaoDAO = linhaEstacaoDAO;
        this.historicoDAO = historicoDAO;
        this.usuarioDAO = usuarioDAO;
        this.relatoDAO = relatoDAO;
        this.consultaDAO = consultaDAO;
        this.clock = clock;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (linhaDAO.count() > 0) {
            return;
        }
        LocalDateTime agora = LocalDateTime.now(clock).truncatedTo(ChronoUnit.SECONDS);

        Linha l1 = linhaDAO.save(new Linha(1, "Azul", "#0455A1", Operadora.METRO));
        Linha l3 = linhaDAO.save(new Linha(3, "Vermelha", "#EE372F", Operadora.METRO));
        Linha l7 = linhaDAO.save(new Linha(7, "Rubi", "#CA016B", Operadora.CPTM));
        Linha l10 = linhaDAO.save(new Linha(10, "Turquesa", "#008B8B", Operadora.CPTM));
        Linha l11 = linhaDAO.save(new Linha(11, "Coral", "#F04E23", Operadora.CPTM));
        Linha l12 = linhaDAO.save(new Linha(12, "Safira", "#083E89", Operadora.CPTM));

        Map<String, Estacao> est = new LinkedHashMap<>();
        for (int i = 0; i < ESTACOES_LINHA_3.size(); i++) {
            String nome = ESTACOES_LINHA_3.get(i);
            Estacao e = estacaoDAO.save(new Estacao(nome, COM_INTEGRACAO.contains(nome), true));
            est.put(nome, e);
            linhaEstacaoDAO.save(new LinhaEstacao(l3, e, i + 1));
        }

        linhaEstacaoDAO.save(new LinhaEstacao(l1, est.get("Sé"), 1));
        linhaEstacaoDAO.save(new LinhaEstacao(l7, est.get("Palmeiras-Barra Funda"), 1));
        linhaEstacaoDAO.save(new LinhaEstacao(l10, est.get("Brás"), 1));
        linhaEstacaoDAO.save(new LinhaEstacao(l11, est.get("Brás"), 1));
        linhaEstacaoDAO.save(new LinhaEstacao(l11, est.get("Tatuapé"), 2));
        linhaEstacaoDAO.save(new LinhaEstacao(l11, est.get("Corinthians-Itaquera"), 3));
        linhaEstacaoDAO.save(new LinhaEstacao(l12, est.get("Brás"), 1));
        linhaEstacaoDAO.save(new LinhaEstacao(l12, est.get("Tatuapé"), 2));

        Usuario carlos = usuario("Carlos Mendes", "carlos.mendes@exemplo.com", PerfilUsuario.TRABALHADOR, 1.20, agora.minusDays(60));
        Usuario maria = usuario("Dona Maria Aparecida", "maria.aparecida@exemplo.com", PerfilUsuario.IDOSO, 1.00, agora.minusDays(45));
        Usuario juliana = usuario("Juliana Rocha", "juliana.rocha@exemplo.com", PerfilUsuario.MOBILIDADE_REDUZIDA, 1.10, agora.minusDays(40));
        Usuario pedro = usuario("Pedro Lima", "pedro.lima@exemplo.com", PerfilUsuario.ESTUDANTE, 0.90, agora.minusDays(30));
        Usuario ana = usuario("Ana Souza", "ana.souza@exemplo.com", PerfilUsuario.TRABALHADOR, 1.30, agora.minusDays(20));
        Usuario roberto = usuario("Roberto Alves", "roberto.alves@exemplo.com", PerfilUsuario.TRABALHADOR, 1.05, agora.minusDays(10));

        List<HistoricoLotacao> historico = new ArrayList<>();
        for (int ordem = 1; ordem <= ESTACOES_LINHA_3.size(); ordem++) {
            Estacao e = est.get(ESTACOES_LINHA_3.get(ordem - 1));
            for (Sentido s : Sentido.values()) {
                for (TipoDia t : TipoDia.values()) {
                    for (int k = 0; k < GeradorHistoricoSimulado.TOTAL_FAIXAS; k++) {
                        historico.add(new HistoricoLotacao(e, l3, s, t, GeradorHistoricoSimulado.faixa(k),
                                GeradorHistoricoSimulado.indice(ordem, s, t, k)));
                    }
                }
            }
        }
        historicoDAO.saveAll(historico);

        relato(carlos, est.get("Artur Alvim"), l3, SENTIDO_BARRA_FUNDA, NivelLotacao.CHEIO, agora.minusMinutes(3));
        relato(ana, est.get("Brás"), l3, SENTIDO_BARRA_FUNDA, NivelLotacao.LOTADO, agora.minusMinutes(5));
        relato(roberto, est.get("Tatuapé"), l3, SENTIDO_BARRA_FUNDA, NivelLotacao.LOTADO, agora.minusMinutes(8));
        relato(pedro, est.get("Sé"), l3, SENTIDO_ITAQUERA, NivelLotacao.MODERADO, agora.minusMinutes(12));
        relato(maria, est.get("Penha"), l3, SENTIDO_BARRA_FUNDA, NivelLotacao.CHEIO, agora.minusMinutes(15));
        relato(juliana, est.get("Carrão"), l3, SENTIDO_BARRA_FUNDA, NivelLotacao.CHEIO, agora.minusMinutes(18));
        relato(carlos, est.get("Belém"), l3, SENTIDO_BARRA_FUNDA, NivelLotacao.LOTADO, agora.minusMinutes(20));
        relato(ana, est.get("Bresser-Mooca"), l3, SENTIDO_ITAQUERA, NivelLotacao.MODERADO, agora.minusMinutes(25));
        relato(roberto, est.get("República"), l3, SENTIDO_ITAQUERA, NivelLotacao.VAZIO, agora.minusMinutes(28));
        relato(pedro, est.get("Corinthians-Itaquera"), l3, SENTIDO_BARRA_FUNDA, NivelLotacao.MODERADO, agora.minusMinutes(40));
        relato(juliana, est.get("Sé"), l3, SENTIDO_BARRA_FUNDA, NivelLotacao.LOTADO, agora.minusMinutes(60));
        relato(maria, est.get("Santa Cecília"), l3, SENTIDO_ITAQUERA, NivelLotacao.VAZIO, agora.minusMinutes(120));

        consulta(carlos, est.get("Artur Alvim"), est.get("Sé"), SENTIDO_BARRA_FUNDA, "07:00", "06:00", 96, 50, est.get("Brás"), true, agora.minusDays(5));
        consulta(maria, est.get("Penha"), est.get("República"), SENTIDO_BARRA_FUNDA, "07:30", "06:15", 94, 55, est.get("Brás"), true, agora.minusDays(4));
        consulta(juliana, est.get("Tatuapé"), est.get("Anhangabaú"), SENTIDO_BARRA_FUNDA, "08:00", "06:30", 93, 60, est.get("Brás"), null, agora.minusDays(3));
        consulta(pedro, est.get("Sé"), est.get("Corinthians-Itaquera"), SENTIDO_ITAQUERA, "18:00", "19:30", 97, 64, est.get("Brás"), false, agora.minusDays(2));
        consulta(ana, est.get("Corinthians-Itaquera"), est.get("Brás"), SENTIDO_BARRA_FUNDA, "07:15", "06:00", 93, 47, est.get("Bresser-Mooca"), true, agora.minusDays(1));
        consulta(roberto, est.get("Brás"), est.get("Tatuapé"), SENTIDO_ITAQUERA, "18:15", "19:45", 96, 58, est.get("Brás"), true, agora.minusHours(2));

        log.info("DevDataSeeder: {} linhas, {} estações, {} históricos, {} usuários, {} relatos, {} consultas.",
                linhaDAO.count(), estacaoDAO.count(), historicoDAO.count(), usuarioDAO.count(),
                relatoDAO.count(), consultaDAO.count());
    }

    private Usuario usuario(String nome, String email, PerfilUsuario perfil, double reputacao, LocalDateTime cadastro) {
        return usuarioDAO.save(new Usuario(nome, email, perfil, reputacao, cadastro));
    }

    private void relato(Usuario u, Estacao e, Linha l, Sentido s, NivelLotacao n, LocalDateTime quando) {
        relatoDAO.save(new RelatoLotacao(u, e, l, s, n, quando));
    }

    private void consulta(Usuario u, Estacao origem, Estacao destino, Sentido sentido, String desejado,
                          String sugerido, int indDesejado, int indSugerido, Estacao critica, Boolean aceita,
                          LocalDateTime quando) {
        ConsultaViagem c = new ConsultaViagem();
        c.setUsuario(u);
        c.setOrigem(origem);
        c.setDestino(destino);
        c.setSentido(sentido);
        c.setDataViagem(quando.toLocalDate());
        c.setHorarioDesejado(desejado);
        c.setHorarioSugerido(sugerido);
        c.setIndiceDesejado(indDesejado);
        c.setIndiceSugerido(indSugerido);
        c.setEstacaoCritica(critica);
        c.setSugestaoAceita(aceita);
        c.setDataConsulta(quando);
        consultaDAO.save(c);
    }
}
