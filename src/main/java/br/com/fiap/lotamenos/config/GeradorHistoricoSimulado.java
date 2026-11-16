package br.com.fiap.lotamenos.config;

import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;

public final class GeradorHistoricoSimulado {
    public static final int TOTAL_FAIXAS = 76;
    public static final int PRIMEIRA_FAIXA_MINUTOS = 5 * 60;

    private static final int[] PESO_MANHA = {0, 40, 45, 52, 62, 70, 94, 95, 100, 98, 97, 96, 94, 90, 86, 82, 78, 74, 70};

    private static final int[] PESO_TARDE = {0, 65, 70, 75, 85, 90, 97, 99, 100, 98, 97, 95, 85, 80, 75, 70, 66, 62, 20};

    private static final int[] CURVA_MANHA = {30, 35, 40, 45, 50, 56, 62, 80, 96, 100, 100, 100, 97, 90, 80, 68};

    private static final int[] CURVA_TARDE = {70, 80, 92, 100, 100, 100, 97, 90, 82, 74, 66, 58};

    private GeradorHistoricoSimulado() {
    }

    public static String faixa(int k) {
        int minutos = PRIMEIRA_FAIXA_MINUTOS + k * 15;
        return String.format("%02d:%02d", minutos / 60, minutos % 60);
    }

    public static int indice(int ordem, Sentido sentido, TipoDia tipoDia, int k) {
        int minutos = PRIMEIRA_FAIXA_MINUTOS + k * 15;
        int bonus = Math.max(0, 10 - Math.abs(ordem - 7));
        boolean rumoBarraFunda = sentido == Sentido.SENTIDO_BARRA_FUNDA;
        int base;
        if (minutos < 9 * 60) {
            if (rumoBarraFunda) {
                base = Math.max(20, arred(96 * PESO_MANHA[ordem] * CURVA_MANHA[k], 10000));
            } else if (minutos < 6 * 60) {
                base = 20 + bonus;
            } else {
                base = 40 + bonus;
            }
        } else if (minutos < 17 * 60) {
            base = 40 + bonus;
        } else if (minutos < 20 * 60) {
            if (!rumoBarraFunda) {
                base = Math.max(40 + bonus, arred(96 * PESO_TARDE[ordem] * CURVA_TARDE[k - 48], 10000));
            } else {
                base = 40 + bonus;
            }
        } else if (minutos < 21 * 60) {
            base = 30 + bonus;
        } else {
            base = 19 + bonus;
        }
        int fator = switch (tipoDia) {
            case UTIL -> 100;
            case SABADO -> 55;
            case DOMINGO -> 40;
        };
        int valor = arred(base * fator, 100);
        int ruido = (ordem * 7 + k * 3) % 9 - 4;
        return Math.max(0, Math.min(100, valor + ruido));
    }

    private static int arred(int numerador, int divisor) {
        return (numerador + divisor / 2) / divisor;
    }
}
