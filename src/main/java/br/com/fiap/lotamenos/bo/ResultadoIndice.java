package br.com.fiap.lotamenos.bo;

/**
 * Resultado detalhado do cálculo do Índice LotaMenos.
 *
 * @param indice              índice final (0–100)
 * @param base                índice histórico da faixa (ou 30 se não houver histórico)
 * @param relatosConsiderados quantos relatos tiveram peso maior que zero
 * @param pesoRelatos         w = min(0.6, 0.15 × soma dos pesos)
 */
public record ResultadoIndice(int indice, int base, int relatosConsiderados, double pesoRelatos) {
}
