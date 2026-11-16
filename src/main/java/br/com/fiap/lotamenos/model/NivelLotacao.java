package br.com.fiap.lotamenos.model;

/**
 * Nível de lotação, com a faixa de índice correspondente (0–100) e o valor
 * numérico usado quando um passageiro relata esse nível.
 */
public enum NivelLotacao {
    VAZIO(0, 39, 20),
    MODERADO(40, 64, 50),
    CHEIO(65, 84, 75),
    LOTADO(85, 100, 95);

    private final int indiceMinimo;
    private final int indiceMaximo;
    private final int valor;

    NivelLotacao(int indiceMinimo, int indiceMaximo, int valor) {
        this.indiceMinimo = indiceMinimo;
        this.indiceMaximo = indiceMaximo;
        this.valor = valor;
    }

    /** Converte um índice (0–100) no nível correspondente. Valores fora da faixa são limitados. */
    public static NivelLotacao deIndice(int indice) {
        int limitado = Math.max(0, Math.min(100, indice));
        for (NivelLotacao nivel : values()) {
            if (limitado <= nivel.indiceMaximo) {
                return nivel;
            }
        }
        return LOTADO;
    }

    public int getIndiceMinimo() {
        return indiceMinimo;
    }

    public int getIndiceMaximo() {
        return indiceMaximo;
    }

    public int getValor() {
        return valor;
    }
}
