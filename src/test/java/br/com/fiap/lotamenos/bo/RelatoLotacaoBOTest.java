package br.com.fiap.lotamenos.bo;

import br.com.fiap.lotamenos.dao.EstacaoDAO;
import br.com.fiap.lotamenos.dao.LinhaDAO;
import br.com.fiap.lotamenos.dao.LinhaEstacaoDAO;
import br.com.fiap.lotamenos.dao.RelatoLotacaoDAO;
import br.com.fiap.lotamenos.dao.UsuarioDAO;
import br.com.fiap.lotamenos.dto.RelatoRequest;
import br.com.fiap.lotamenos.dto.RelatoResponse;
import br.com.fiap.lotamenos.exception.EstacaoForaDoRecorteException;
import br.com.fiap.lotamenos.exception.RegraNegocioException;
import br.com.fiap.lotamenos.exception.RelatoDuplicadoException;
import br.com.fiap.lotamenos.model.Estacao;
import br.com.fiap.lotamenos.model.Linha;
import br.com.fiap.lotamenos.model.NivelLotacao;
import br.com.fiap.lotamenos.model.Operadora;
import br.com.fiap.lotamenos.model.PerfilUsuario;
import br.com.fiap.lotamenos.model.RelatoLotacao;
import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RelatoLotacaoBOTest {
    private static final ZoneId SP = ZoneId.of("America/Sao_Paulo");
    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 11, 17, 7, 0);

    @Mock private RelatoLotacaoDAO relatoDAO;
    @Mock private UsuarioDAO usuarioDAO;
    @Mock private EstacaoDAO estacaoDAO;
    @Mock private LinhaDAO linhaDAO;
    @Mock private LinhaEstacaoDAO linhaEstacaoDAO;
    @Mock private LotacaoBO lotacaoBO;

    private RelatoLotacaoBO bo;
    private Usuario usuario;
    private Estacao bras;
    private Linha linha3;

    @BeforeEach
    void setUp() {
        bo = new RelatoLotacaoBO(relatoDAO, usuarioDAO, estacaoDAO, linhaDAO, linhaEstacaoDAO, lotacaoBO,
                Clock.fixed(AGORA.atZone(SP).toInstant(), SP));
        usuario = new Usuario("Carlos", "c@exemplo.com", PerfilUsuario.TRABALHADOR, 1.0, AGORA.minusDays(10));
        usuario.setId(1L);
        bras = new Estacao("Brás", true, true);
        bras.setId(8L);
        linha3 = new Linha(3, "Vermelha", "#EE372F", Operadora.METRO);
        linha3.setId(2L);

        when(usuarioDAO.findById(1L)).thenReturn(Optional.of(usuario));
        when(estacaoDAO.findById(8L)).thenReturn(Optional.of(bras));
        when(linhaDAO.findByNumero(3)).thenReturn(Optional.of(linha3));
        when(linhaDAO.findById(2L)).thenReturn(Optional.of(linha3));
        when(linhaEstacaoDAO.existsByLinhaIdAndEstacaoId(2L, 8L)).thenReturn(true);
        when(relatoDAO.existeRelatoNoIntervalo(anyLong(), anyLong(), any(), any())).thenReturn(false);
        when(relatoDAO.save(any(RelatoLotacao.class))).thenAnswer(inv -> {
            RelatoLotacao r = inv.getArgument(0);
            r.setId(50L);
            return r;
        });
    }

    private RelatoRequest req(NivelLotacao nivel, LocalDateTime dataHora) {
        return new RelatoRequest(1L, 8L, null, Sentido.SENTIDO_BARRA_FUNDA, nivel, dataHora);
    }

    @Test
    @DisplayName("Relato coerente (|95 − 90| ≤ 20) → reputação +0.05 e relato salvo com data = agora")
    void reputacaoSobe() {
        when(lotacaoBO.calcularIndice(bras, linha3, Sentido.SENTIDO_BARRA_FUNDA, AGORA)).thenReturn(90);

        RelatoResponse r = bo.registrar(req(NivelLotacao.LOTADO, null));

        assertThat(usuario.getReputacao()).isEqualTo(1.05);
        assertThat(r.reputacaoUsuario()).isEqualTo(1.05);
        assertThat(r.dataHora()).isEqualTo(AGORA);
        verify(usuarioDAO).save(usuario);
        ArgumentCaptor<RelatoLotacao> captor = ArgumentCaptor.forClass(RelatoLotacao.class);
        verify(relatoDAO).save(captor.capture());
        assertThat(captor.getValue().getLinha()).isSameAs(linha3);
    }

    @Test
    @DisplayName("Relato incoerente (|95 − 30| > 20) → reputação −0.10")
    void reputacaoCai() {
        when(lotacaoBO.calcularIndice(any(), any(), any(), any())).thenReturn(30);

        bo.registrar(req(NivelLotacao.LOTADO, null));

        assertThat(usuario.getReputacao()).isEqualTo(0.9);
    }

    @Test
    @DisplayName("Índice é calculado ANTES de salvar o relato (sem ele)")
    void indiceCalculadoAntesDeSalvar() {
        when(lotacaoBO.calcularIndice(any(), any(), any(), any())).thenAnswer(inv -> {
            verify(relatoDAO, never()).save(any());
            return 50;
        });
        bo.registrar(req(NivelLotacao.MODERADO, null));
        verify(relatoDAO).save(any());
    }

    @Test
    @DisplayName("Reputação respeita os limites 0.3 e 1.5")
    void limitesDaReputacao() {
        assertThat(RelatoLotacaoBO.novaReputacao(1.5, 95, 90)).isEqualTo(1.5);
        assertThat(RelatoLotacaoBO.novaReputacao(1.48, 95, 90)).isEqualTo(1.5);
        assertThat(RelatoLotacaoBO.novaReputacao(0.3, 95, 20)).isEqualTo(0.3);
        assertThat(RelatoLotacaoBO.novaReputacao(0.35, 95, 20)).isEqualTo(0.3);
        assertThat(RelatoLotacaoBO.novaReputacao(1.0, 75, 55)).isEqualTo(1.05);
        assertThat(RelatoLotacaoBO.novaReputacao(1.0, 75, 54)).isEqualTo(0.9);
    }

    @Test
    @DisplayName("Antispam: relato da mesma estação há menos de 10 min → 409 e nada é salvo")
    void antispam() {
        when(relatoDAO.existeRelatoNoIntervalo(eq(1L), eq(8L), eq(AGORA.minusMinutes(10)), eq(AGORA.plusMinutes(10))))
                .thenReturn(true);

        assertThatThrownBy(() -> bo.registrar(req(NivelLotacao.CHEIO, null)))
                .isInstanceOf(RelatoDuplicadoException.class)
                .hasMessageContaining("menos de 10 minutos");
        verify(relatoDAO, never()).save(any());
        verify(usuarioDAO, never()).save(any());
        assertThat(usuario.getReputacao()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Data/hora no futuro → RegraNegocioException")
    void dataNoFuturo() {
        assertThatThrownBy(() -> bo.registrar(req(NivelLotacao.CHEIO, AGORA.plusMinutes(5))))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("futuro");
    }

    @Test
    @DisplayName("Estação que não pertence à linha → EstacaoForaDoRecorteException")
    void estacaoForaDaLinha() {
        when(linhaEstacaoDAO.existsByLinhaIdAndEstacaoId(2L, 8L)).thenReturn(false);

        assertThatThrownBy(() -> bo.registrar(req(NivelLotacao.CHEIO, null)))
                .isInstanceOf(EstacaoForaDoRecorteException.class)
                .hasMessage("A estação Brás não faz parte do recorte da Linha 3.");
    }

    @Test
    @DisplayName("Linha diferente da 3 → EstacaoForaDoRecorteException")
    void linhaForaDoRecorte() {
        Linha linha11 = new Linha(11, "Coral", "#F04E23", Operadora.CPTM);
        linha11.setId(5L);
        when(linhaDAO.findById(5L)).thenReturn(Optional.of(linha11));

        RelatoRequest r = new RelatoRequest(1L, 8L, 5L, Sentido.SENTIDO_BARRA_FUNDA, NivelLotacao.CHEIO, null);
        assertThatThrownBy(() -> bo.registrar(r))
                .isInstanceOf(EstacaoForaDoRecorteException.class)
                .hasMessageContaining("Linha 11-Coral");
    }
}
