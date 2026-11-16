package br.com.fiap.lotamenos.model;

/**
 * Perfil do usuário. IDOSO e MOBILIDADE_REDUZIDA ativam a regra de acessibilidade (ODS 10).
 */
public enum PerfilUsuario {
    TRABALHADOR,
    IDOSO,
    MOBILIDADE_REDUZIDA,
    ESTUDANTE;

    /** Indica se o perfil exige viagens com lotação no máximo MODERADA. */
    public boolean exigeAcessibilidade() {
        return this == IDOSO || this == MOBILIDADE_REDUZIDA;
    }
}
