package br.com.fiap.lotamenos.model;

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
