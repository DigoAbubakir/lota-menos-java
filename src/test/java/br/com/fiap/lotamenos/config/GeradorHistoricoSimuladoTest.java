package br.com.fiap.lotamenos.config;

import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeradorHistoricoSimuladoTest {
    private static int k(String hhmm) {
        int minutos = Integer.parseInt(hhmm.substring(0, 2)) * 60 + Integer.parseInt(hhmm.substring(3));
        return (minutos - 300) / 15;
    }

    @Test
    @DisplayName("Faixas de 05:00 a 23:45")
    void faixas() {
        assertThat(GeradorHistoricoSimulado.faixa(0)).isEqualTo("05:00");
        assertThat(GeradorHistoricoSimulado.faixa(75)).isEqualTo("23:45");
    }

    @Test
    @DisplayName("Pico da manhã (07:30, sentido Barra Funda): centro 85–98, extremo leste 60–75")
    void picoManha() {
        for (int ordem = 6; ordem <= 11; ordem++) {
            assertThat(GeradorHistoricoSimulado.indice(ordem, Sentido.SENTIDO_BARRA_FUNDA, TipoDia.UTIL, k("07:30")))
                    .isBetween(85, 100);
        }
        for (int ordem = 17; ordem <= 18; ordem++) {
            assertThat(GeradorHistoricoSimulado.indice(ordem, Sentido.SENTIDO_BARRA_FUNDA, TipoDia.UTIL, k("07:30")))
                    .isBetween(60, 75);
        }
    }

    @Test
    @DisplayName("Pico da tarde (18:00, sentido Itaquera): Sé a Tatuapé 85–98")
    void picoTarde() {
        for (int ordem = 6; ordem <= 11; ordem++) {
            assertThat(GeradorHistoricoSimulado.indice(ordem, Sentido.SENTIDO_ITAQUERA, TipoDia.UTIL, k("18:00")))
                    .isBetween(85, 100);
        }
    }

    @Test
    @DisplayName("Entrepico e contrafluxo 35–55; noite 15–35; fim de semana mais vazio")
    void foraDoPico() {
        for (int ordem = 1; ordem <= 18; ordem++) {
            assertThat(GeradorHistoricoSimulado.indice(ordem, Sentido.SENTIDO_BARRA_FUNDA, TipoDia.UTIL, k("12:00")))
                    .isBetween(35, 55);
            assertThat(GeradorHistoricoSimulado.indice(ordem, Sentido.SENTIDO_ITAQUERA, TipoDia.UTIL, k("07:30")))
                    .isBetween(35, 55);
            assertThat(GeradorHistoricoSimulado.indice(ordem, Sentido.SENTIDO_BARRA_FUNDA, TipoDia.UTIL, k("22:00")))
                    .isBetween(15, 35);
        }
        int util = GeradorHistoricoSimulado.indice(8, Sentido.SENTIDO_BARRA_FUNDA, TipoDia.UTIL, k("07:30"));
        int domingo = GeradorHistoricoSimulado.indice(8, Sentido.SENTIDO_BARRA_FUNDA, TipoDia.DOMINGO, k("07:30"));
        assertThat(domingo).isLessThan(util / 2 + 5);
    }
}
