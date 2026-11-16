package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.HistoricoLotacaoDAO;
import br.com.fiap.lotamenos.dao.RelatoLotacaoDAO;
import br.com.fiap.lotamenos.model.Estacao;
import br.com.fiap.lotamenos.model.HistoricoLotacao;
import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Operadora;
import br.com.fiap.lotamenos.model.PerfilUsuario;
import br.com.fiap.lotamenos.model.RelatoLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LotacaoBOTest {
    private static final ZoneId SP = ZoneId.of("America/Sao_Paulo");

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 17, 7, 0);

    @Mock
    private HistoricoLotacaoDAO historicoDAO;
    @Mock
    private RelatoLotacaoDAO relatoDAO;

    private LotacaoBO bo;
    private Estacao bras;
    private Linha linha3;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(AGORA.atZone(SP).toInstant(), SP);
        bo = new LotacaoBO(historicoDAO, relatoDAO, clock);
        bras = new Estacao("Brás", true, true);
        bras.setId(8L);
        linha3 = new Linha(3, "Vermelha", "#EE372F", Operadora.METRO);
        linha3.setId(2L);
        when(relatoDAO.buscarRecentes(anyLong(), anyLong(), any(), any(), any())).thenReturn(List.of());
    }

    private void historico(int indice) {
        HistoricoLotacao h = new HistoricoLotacao(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, TipoDia.UTIL, "07:00", indice);
        when(historicoDAO.findByEstacaoIdAndLinhaIdAndSentidoAndTipoDiaAndFaixaHorario(
                anyLong(), anyLong(), any(), any(), anyString())).thenReturn(Optional.of(h));
    }

    private void relatos(RelatoLotacao... relatos) {
        when(relatoDAO.buscarRecentes(anyLong(), anyLong(), any(), any(), any())).thenReturn(List.of(relatos));
    }

    private RelatoLotacao relato(double reputacao, NivelLotacao nivel, int minutosAtras) {
        Usuario u = new Usuario("Teste", "t@exemplo.com", PerfilUsuario.TRABALHADOR, reputacao, AGORA.minusDays(1));
        return new RelatoLotacao(u, bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, nivel, AGORA.minusMinutes(minutosAtras));
    }

    @Test
    @DisplayName("Sem histórico para a faixa → índice 30")
    void semHistoricoUsaTrinta() {
        when(historicoDAO.findByEstacaoIdAndLinhaIdAndSentidoAndTipoDiaAndFaixaHorario(
                anyLong(), anyLong(), any(), any(), anyString())).thenReturn(Optional.empty());

        assertThat(bo.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA)).isEqualTo(30);
    }

    @Test
    @DisplayName("Com histórico e sem relatos → índice = base")
    void semRelatosIndiceIgualBase() {
        historico(80);

        assertThat(bo.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA)).isEqualTo(80);
    }

    @Test
    @DisplayName("Faixa arredondada para baixo (07:08 → 07:00) e tipo de dia pela data")
    void faixaArredondadaParaBaixoETipoDia() {
        historico(80);
        bo.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA.plusMinutes(8));
        verify(historicoDAO).findByEstacaoIdAndLinhaIdAndSentidoAndTipoDiaAndFaixaHorario(
                8L, 2L, Sentido.SENTIDO_BARRA_FUNDA, TipoDia.UTIL, "07:00");

        LocalDateTime sabado = LocalDateTime.of(2026, 11, 21, 18, 44);
        bo.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, sabado);
        verify(historicoDAO).findByEstacaoIdAndLinhaIdAndSentidoAndTipoDiaAndFaixaHorario(
                8L, 2L, Sentido.SENTIDO_BARRA_FUNDA, TipoDia.SABADO, "18:30");
    }

    @Test
    @DisplayName("Um relato recente: w = 0.15 → round(60×0.85 + 95×0.15) = 65")
    void comRelatoRecente() {
        historico(60);
        relatos(relato(1.0, NivelLotacao.LOTADO, 0));

        ResultadoIndice r = bo.calcularDetalhado(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA);

        assertThat(r.indice()).isEqualTo(65);
        assertThat(r.base()).isEqualTo(60);
        assertThat(r.relatosConsiderados()).isEqualTo(1);
        assertThat(r.pesoRelatos()).isEqualTo(0.15);
    }

    @Test
    @DisplayName("Decaimento: relato de 15 min pesa metade; relato de 30 min não pesa")
    void decaimento() {
        historico(60);
        relatos(relato(1.0, NivelLotacao.LOTADO, 15));

        assertThat(bo.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA)).isEqualTo(63);

        relatos(relato(1.0, NivelLotacao.LOTADO, 30));
        assertThat(bo.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA)).isEqualTo(60);
    }

    @Test
    @DisplayName("Reputação pondera a média dos relatos")
    void reputacaoPondera() {
        historico(60);
        relatos(relato(1.5, NivelLotacao.VAZIO, 0), relato(0.3, NivelLotacao.LOTADO, 0));

        assertThat(bo.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA)).isEqualTo(53);
    }

    @Test
    @DisplayName("Peso dos relatos limitado a 0.6")
    void pesoLimitado() {
        historico(20);
        List<RelatoLotacao> muitos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            muitos.add(relato(1.5, NivelLotacao.LOTADO, 0));
        }
        relatos(muitos.toArray(new RelatoLotacao[0]));

        ResultadoIndice r = bo.calcularDetalhado(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA);
        assertThat(r.indice()).isEqualTo(65);
        assertThat(r.pesoRelatos()).isEqualTo(0.6);
    }

    @Test
    @DisplayName("Horário a mais de 30 min de agora → relatos ignorados")
    void horarioDistanteIgnoraRelatos() {
        historico(70);
        relatos(relato(1.0, NivelLotacao.VAZIO, 0));

        int indice = bo.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA.plusHours(2));

        assertThat(indice).isEqualTo(70);
        verify(relatoDAO, never()).buscarRecentes(anyLong(), anyLong(), any(), any(), any());
    }

    @Test
    @DisplayName("Horário até 30 min no futuro ainda considera relatos")
    void horarioProximoConsideraRelatos() {
        historico(60);
        relatos(relato(1.0, NivelLotacao.LOTADO, 0));

        assertThat(bo.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA.plusMinutes(25))).isEqualTo(65);
        verify(relatoDAO).buscarRecentes(eq(8L), eq(2L), eq(Sentido.SENTIDO_BARRA_FUNDA),
                eq(AGORA.minusMinutes(30)), eq(AGORA));
    }

    @Test
    @DisplayName("NivelLotacao.deIndice respeita as faixas")
    void nivelDeIndice() {
        assertThat(NivelLotacao.deIndice(39)).isEqualTo(NivelLotacao.VAZIO);
        assertThat(NivelLotacao.deIndice(40)).isEqualTo(NivelLotacao.MODERADO);
        assertThat(NivelLotacao.deIndice(64)).isEqualTo(NivelLotacao.MODERADO);
        assertThat(NivelLotacao.deIndice(65)).isEqualTo(NivelLotacao.CHEIO);
        assertThat(NivelLotacao.deIndice(85)).isEqualTo(NivelLotacao.LOTADO);
        assertThat(NivelLotacao.deIndice(150)).isEqualTo(NivelLotacao.LOTADO);
    }
}
