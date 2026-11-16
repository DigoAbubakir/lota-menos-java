package br.com.fiap.lotamenos.model;

public enum PerfilUsuario {
    TRABALHADOR,
    IDOSO,
    MOBILIDADE_REDUZIDA,
    ESTUDANTE;

    public boolean exigeAcessibilidade() {
        return this == IDOSO || this == MOBILIDADE_REDUZIDA;
    }
}
