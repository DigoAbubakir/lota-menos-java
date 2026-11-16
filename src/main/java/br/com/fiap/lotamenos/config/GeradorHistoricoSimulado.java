package br.com.fiap.lotamenos.config;

import br.com.fiap.lotamenos.model.Sentido;
import br.com.fiap.lotamenos.model.TipoDia;

/**
 * Gera o índice histórico SIMULADO de lotação da Linha 3-Vermelha.
 * <p>
 * É a mesma fórmula do bloco PL/SQL de {@code database/02_dml.sql} (aritmética inteira idêntica),
 * para os resultados no H2 (perfil dev) e no Oracle FIAP serem iguais.
 * <ul>
 *   <li>Faixas de 05:00 a 23:45 (76 faixas de 15 min; índice k = 0..75).</li>
 *   <li>Pico da manhã (05:00–08:45) no sentido Barra Funda: perfil por estação (máximo entre Tatuapé e Sé)
 *       × curva no tempo (sobe a partir das 06:00, auge 07:15–07:45).</li>
 *   <li>Pico da tarde (17:00–19:45) no sentido Itaquera: máximo entre Sé e Tatuapé, caindo rumo ao leste.</li>
 *   <li>Contrafluxo e entrepico: 35–55. 20:00–20:45: transição. Após 21:00: 15–35.</li>
 *   <li>Sábado = 55% do dia útil; domingo = 40%.</li>
 *   <li>Variação determinística: MOD(ordem × 7 + k × 3, 9) − 4.</li>
 * </ul>
 */
public final class GeradorHistoricoSimulado {

    public static final int TOTAL_FAIXAS = 76;
    public static final int PRIMEIRA_FAIXA_MINUTOS = 5 * 60;

    /** Peso (%) de cada estação (índice = ordem 1..18) no pico da manhã, sentido Barra Funda. */
    private static final int[] PESO_MANHA = {0, 40, 45, 52, 62, 70, 94, 95, 100, 98, 97, 96, 94, 90, 86, 82, 78, 74, 70};
    /** Peso (%) de cada estação no pico da tarde, sentido Itaquera. */
    private static final int[] PESO_TARDE = {0, 65, 70, 75, 85, 90, 97, 99, 100, 98, 97, 95, 85, 80, 75, 70, 66, 62, 20};
    /** Curva (%) da manhã: faixas 05:00 (k=0) a 08:45 (k=15). */
    private static final int[] CURVA_MANHA = {30, 35, 40, 45, 50, 56, 62, 80, 96, 100, 100, 100, 97, 90, 80, 68};
    /** Curva (%) da tarde: faixas 17:00 (k=48) a 19:45 (k=59). */
    private static final int[] CURVA_TARDE = {70, 80, 92, 100, 100, 100, 97, 90, 82, 74, 66, 58};

    private GeradorHistoricoSimulado() {
    }

    /** "HH:mm" da faixa k (0 = 05:00 ... 75 = 23:45). */
    public static String faixa(int k) {
        int minutos = PRIMEIRA_FAIXA_MINUTOS + k * 15;
        return String.format("%02d:%02d", minutos / 60, minutos % 60);
    }

    /**
     * Índice simulado (0–100).
     *
     * @param ordem   ordem da estação na Linha 3 (1 = Palmeiras-Barra Funda ... 18 = Corinthians-Itaquera)
     * @param sentido sentido do trem
     * @param tipoDia tipo de dia
     * @param k       índice da faixa (0..75)
     */
    public static int indice(int ordem, Sentido sentido, TipoDia tipoDia, int k) {
        int minutos = PRIMEIRA_FAIXA_MINUTOS + k * 15;
        int bonus = Math.max(0, 10 - Math.abs(ordem - 7)); // centralidade: estações perto da Sé/Pedro II
        boolean rumoBarraFunda = sentido == Sentido.SENTIDO_BARRA_FUNDA;
        int base;
        if (minutos < 9 * 60) {                       // 05:00–08:45
            if (rumoBarraFunda) {
                base = Math.max(20, arred(96 * PESO_MANHA[ordem] * CURVA_MANHA[k], 10000));
            } else if (minutos < 6 * 60) {
                base = 20 + bonus;
            } else {
                base = 40 + bonus;                    // contrafluxo
            }
        } else if (minutos < 17 * 60) {               // entrepico 09:00–16:45
            base = 40 + bonus;
        } else if (minutos < 20 * 60) {               // 17:00–19:45
            if (!rumoBarraFunda) {
                base = Math.max(40 + bonus, arred(96 * PESO_TARDE[ordem] * CURVA_TARDE[k - 48], 10000));
            } else {
                base = 40 + bonus;                    // contrafluxo
            }
        } else if (minutos < 21 * 60) {               // 20:00–20:45
            base = 30 + bonus;
        } else {                                      // noite
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

    /** Divisão arredondada (meio para cima), igual ao ROUND do Oracle para positivos. */
    private static int arred(int numerador, int divisor) {
        return (numerador + divisor / 2) / divisor;
    }
}
