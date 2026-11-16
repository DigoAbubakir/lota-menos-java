package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.ConsultaViagemDAO;
import br.com.fiap.lotamenos.dao.EstacaoDAO;
import br.com.fiap.lotamenos.dao.LinhaDAO;
import br.com.fiap.lotamenos.dao.LinhaEstacaoDAO;
import br.com.fiap.lotamenos.dao.UsuarioDAO;
import br.com.fiap.lotamenos.dto.PlanejarViagemRequest;
import br.com.fiap.lotamenos.dto.ViagemResponse;
import br.com.fiap.lotamenos.exception.EstacaoForaDoRecorteException;
import br.com.fiap.lotamenos.exception.JanelaInvalidaException;
import br.com.fiap.lotamenos.exception.RegraNegocioException;
import br.com.fiap.lotamenos.model.ConsultaViagem;
import br.com.fiap.lotamenos.model.Estacao;
import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.LinhaEstacao;
import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Operadora;
import br.com.fiap.lotamenos.model.PerfilUsuario;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ViagemBOTest {

    private static final ZoneId SP = ZoneId.of("America/Sao_Paulo");
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 16, 12, 0);
    private static final LocalDate DATA = LocalDate.of(2026, 11, 17);

    @Mock private ConsultaViagemDAO consultaDAO;
    @Mock private UsuarioDAO usuarioDAO;
    @Mock private EstacaoDAO estacaoDAO;
    @Mock private LinhaDAO linhaDAO;
    @Mock private LinhaEstacaoDAO linhaEstacaoDAO;
    @Mock private LotacaoBO lotacaoBO;

    private ViagemBO bo;
    private Linha linha3;
    private final List<Estacao> estacoes = new ArrayList<>();
    private final List<LinhaEstacao> linhaEstacoes = new ArrayList<>();
    /** estacaoId → (faixa "HH:mm" → índice) */
    private final Map<Long, Map<String, Integer>> historico = new HashMap<>();
    private Usuario trabalhador;
    private Usuario idosa;

    @BeforeEach
    void setUp() {
        bo = new ViagemBO(consultaDAO, usuarioDAO, estacaoDAO, linhaDAO, linhaEstacaoDAO, lotacaoBO,
                Clock.fixed(AGORA.atZone(SP).toInstant(), SP));

        linha3 = new Linha(3, "Vermelha", "#EE372F", Operadora.METRO);
        linha3.setId(2L);
        when(linhaDAO.findByNumero(3)).thenReturn(Optional.of(linha3));

        // Linha fictícia de 5 estações: A(1) B(2) C(3) D(4) E(5)
        String[] nomes = {"A", "B", "C", "D", "E"};
        for (int i = 0; i < nomes.length; i++) {
            Estacao e = new Estacao(nomes[i], false, true);
            e.setId(101L + i);
            estacoes.add(e);
            LinhaEstacao le = new LinhaEstacao(linha3, e, i + 1);
            linhaEstacoes.add(le);
            when(estacaoDAO.findById(e.getId())).thenReturn(Optional.of(e));
            when(linhaEstacaoDAO.findByLinhaIdAndEstacaoId(2L, e.getId())).thenReturn(Optional.of(le));
        }
        when(linhaEstacaoDAO.listarPorLinhaEmOrdem(2L)).thenReturn(linhaEstacoes);

        trabalhador = new Usuario("Carlos", "c@exemplo.com", PerfilUsuario.TRABALHADOR, 1.0, AGORA);
        trabalhador.setId(1L);
        idosa = new Usuario("Maria", "m@exemplo.com", PerfilUsuario.IDOSO, 1.0, AGORA);
        idosa.setId(2L);
        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(trabalhador));
        when(usuarioDAO.findById(2L)).thenReturn(Optional.of(idosa));

        when(lotacaoBO.carregarMapa(any(), any(), any()))
                .thenAnswer(inv -> new MapaLotacao(DATA, historico, Map.of(), AGORA));
        when(consultaDAO.save(any(ConsultaViagem.class))).thenAnswer(inv -> {
            ConsultaViagem c = inv.getArgument(0);
            c.setId(99L);
            return c;
        });
    }

    private Estacao est(String nome) {
        return estacoes.stream().filter(e -> e.getNome().equals(nome)).findFirst().orElseThrow();
    }

    private void indice(String estacao, String faixa, int valor) {
        historico.computeIfAbsent(est(estacao).getId(), k -> new HashMap<>()).put(faixa, valor);
    }

    /** Preenche todas as estações em todas as faixas de 06:00 a 09:00 com o mesmo valor. */
    private void preencherTudo(int valor) {
        for (Estacao e : estacoes) {
            for (int m = 6 * 60; m <= 9 * 60; m += 15) {
                indice(e.getNome(), HorarioUtil.formatar(HorarioUtil.deMinutos(m)), valor);
            }
        }
    }

    private PlanejarViagemRequest req(long usuario, String origem, String destino, String ini, String fim, String desejado) {
        return new PlanejarViagemRequest(usuario, est(origem).getId(), est(destino).getId(), ini, fim, desejado, DATA);
    }

    @Test
    @DisplayName("Sentido: destino com ordem menor → Barra Funda; maior → Itaquera")
    void sentidoPelaOrdem() {
        preencherTudo(50);
        assertThat(bo.planejar(req(1, "E", "A", "07:00", "07:30", null)).sentido()).isEqualTo(Sentido.SENTIDO_BARRA_FUNDA);
        assertThat(bo.planejar(req(1, "A", "E", "07:00", "07:30", null)).sentido()).isEqualTo(Sentido.SENTIDO_ITAQUERA);
        verify(lotacaoBO).carregarMapa(linha3, Sentido.SENTIDO_BARRA_FUNDA, DATA);
        verify(lotacaoBO).carregarMapa(linha3, Sentido.SENTIDO_ITAQUERA, DATA);
    }

    @Test
    @DisplayName("Índice da viagem = maior do trecho; destino fica fora do trecho")
    void estacaoCriticaEhOMaiorDoTrecho() {
        preencherTudo(40);
        // Trecho E → B (sentido Barra Funda): E, D, C (B é o destino e não conta)
        indice("E", "07:00", 50);
        indice("D", "07:00", 80);
        indice("C", "07:00", 70);
        indice("B", "07:00", 99);

        ViagemResponse r = bo.planejar(req(1, "E", "B", "07:00", "07:15", "07:00"));

        assertThat(r.indiceDesejado()).isEqualTo(80);
        assertThat(r.horarioSugerido()).isEqualTo("07:15");
        assertThat(r.indiceSugerido()).isEqualTo(40);
        assertThat(r.estacaoCritica()).isEqualTo("E"); // empate em 40 → primeira estação do trecho
        assertThat(r.reducaoPontos()).isEqualTo(40);
    }

    @Test
    @DisplayName("Escolhe a faixa de menor índice da janela e monta a dica")
    void melhorFaixaDaJanela() {
        preencherTudo(90);
        indice("D", "06:30", 58);
        indice("C", "06:30", 55);
        indice("E", "06:30", 50);
        indice("B", "06:30", 40);

        ViagemResponse r = bo.planejar(req(1, "E", "A", "06:00", "08:00", "07:00"));

        assertThat(r.horarioDesejado()).isEqualTo("07:00");
        assertThat(r.indiceDesejado()).isEqualTo(90);
        assertThat(r.nivelDesejado()).isEqualTo(NivelLotacao.LOTADO);
        assertThat(r.horarioSugerido()).isEqualTo("06:30");
        assertThat(r.indiceSugerido()).isEqualTo(58);
        assertThat(r.nivelSugerido()).isEqualTo(NivelLotacao.MODERADO);
        assertThat(r.estacaoCritica()).isEqualTo("D");
        assertThat(r.reducaoPontos()).isEqualTo(32);
        assertThat(r.dica()).isEqualTo("Saindo às 06:30 em vez de 07:00 você evita o pico. Trecho mais cheio: D.");
        verify(consultaDAO).save(any(ConsultaViagem.class));
    }

    @Test
    @DisplayName("Empate de índice → mais próxima do horário desejado; empate de distância → mais cedo")
    void desempate() {
        preencherTudo(90);
        for (String e : List.of("E", "D", "C", "B")) {
            indice(e, "06:30", 60);
            indice(e, "07:30", 60);
        }
        assertThat(bo.planejar(req(1, "E", "A", "06:00", "08:00", "07:15")).horarioSugerido()).isEqualTo("07:30");

        historico.clear();
        preencherTudo(90);
        for (String e : List.of("E", "D", "C", "B")) {
            indice(e, "06:45", 60);
            indice(e, "07:15", 60);
        }
        assertThat(bo.planejar(req(1, "E", "A", "06:00", "08:00", "07:00")).horarioSugerido()).isEqualTo("06:45");
    }

    @Test
    @DisplayName("Horário desejado já é o melhor → dica específica")
    void horarioDesejadoJaEhOMelhor() {
        preencherTudo(80);
        for (String e : List.of("E", "D", "C", "B")) {
            indice(e, "07:00", 45);
        }
        ViagemResponse r = bo.planejar(req(1, "E", "A", "06:00", "08:00", "07:00"));
        assertThat(r.horarioSugerido()).isEqualTo("07:00");
        assertThat(r.reducaoPontos()).isZero();
        assertThat(r.dica()).startsWith("Seu horário já é o melhor da janela.");
    }

    @Test
    @DisplayName("Acessibilidade: IDOSO sem faixa ≤ MODERADO → melhor disponível + alerta")
    void acessibilidadeSemOpcaoConfortavel() {
        preencherTudo(90);
        indice("E", "06:15", 70);

        ViagemResponse r = bo.planejar(req(2, "E", "D", "06:00", "07:00", "06:30"));

        assertThat(r.horarioSugerido()).isEqualTo("06:15");
        assertThat(r.indiceSugerido()).isEqualTo(70);
        assertThat(r.alertaAcessibilidade()).isTrue();
        assertThat(r.dica()).contains("Atenção");
    }

    @Test
    @DisplayName("Acessibilidade: IDOSO com faixa ≤ MODERADO → sem alerta; TRABALHADOR nunca recebe alerta")
    void acessibilidadeComOpcaoConfortavel() {
        preencherTudo(90);
        indice("E", "06:45", 64);

        ViagemResponse idoso = bo.planejar(req(2, "E", "D", "06:00", "07:00", "06:00"));
        assertThat(idoso.horarioSugerido()).isEqualTo("06:45");
        assertThat(idoso.alertaAcessibilidade()).isFalse();

        historico.clear();
        preencherTudo(90);
        ViagemResponse trabalhadorResp = bo.planejar(req(1, "E", "D", "06:00", "07:00", "06:00"));
        assertThat(trabalhadorResp.alertaAcessibilidade()).isFalse();
    }

    @Test
    @DisplayName("Janela inválida: fim antes do início, mais de 3 h ou desejado fora da janela")
    void janelaInvalida() {
        preencherTudo(50);
        assertThatThrownBy(() -> bo.planejar(req(1, "E", "A", "08:00", "07:00", null)))
                .isInstanceOf(JanelaInvalidaException.class).hasMessageContaining("depois do início");
        assertThatThrownBy(() -> bo.planejar(req(1, "E", "A", "07:00", "07:00", null)))
                .isInstanceOf(JanelaInvalidaException.class);
        assertThatThrownBy(() -> bo.planejar(req(1, "E", "A", "05:00", "08:15", null)))
                .isInstanceOf(JanelaInvalidaException.class).hasMessageContaining("3 horas");
        assertThatThrownBy(() -> bo.planejar(req(1, "E", "A", "06:00", "07:00", "07:30")))
                .isInstanceOf(JanelaInvalidaException.class).hasMessageContaining("dentro da janela");
        verify(consultaDAO, never()).save(any());
    }

    @Test
    @DisplayName("Janela de exatamente 3 h é aceita")
    void janelaDeTresHorasAceita() {
        preencherTudo(50);
        assertThat(bo.planejar(req(1, "E", "A", "06:00", "09:00", null)).consultaId()).isEqualTo(99L);
    }

    @Test
    @DisplayName("Estação fora da Linha 3 → EstacaoForaDoRecorteException")
    void estacaoForaDoRecorte() {
        Estacao luz = new Estacao("Luz", true, true);
        luz.setId(500L);
        when(estacaoDAO.findById(500L)).thenReturn(Optional.of(luz));
        when(linhaEstacaoDAO.findByLinhaIdAndEstacaoId(2L, 500L)).thenReturn(Optional.empty());

        PlanejarViagemRequest r = new PlanejarViagemRequest(1L, 500L, est("A").getId(), "07:00", "08:00", null, DATA);
        assertThatThrownBy(() -> bo.planejar(r))
                .isInstanceOf(EstacaoForaDoRecorteException.class)
                .hasMessage("A estação Luz não faz parte do recorte da Linha 3.");
        verify(consultaDAO, never()).save(any());
    }

    @Test
    @DisplayName("Origem igual ao destino → RegraNegocioException")
    void origemIgualDestino() {
        assertThatThrownBy(() -> bo.planejar(req(1, "C", "C", "07:00", "08:00", null)))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("diferentes");
    }

    @Test
    @DisplayName("Data não informada → usa hoje")
    void dataPadraoHoje() {
        preencherTudo(50);
        PlanejarViagemRequest r = new PlanejarViagemRequest(1L, est("E").getId(), est("A").getId(),
                "07:00", "08:00", null, null);
        bo.planejar(r);
        verify(lotacaoBO).carregarMapa(eq(linha3), eq(Sentido.SENTIDO_BARRA_FUNDA), eq(AGORA.toLocalDate()));
    }
}
