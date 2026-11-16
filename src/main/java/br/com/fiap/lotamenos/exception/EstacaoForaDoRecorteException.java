package br.com.fiap.lotamenos.exception;

/**
 * Estação (ou linha) fora do recorte do projeto, que é a Linha 3-Vermelha → HTTP 422.
 */
public class EstacaoForaDoRecorteException extends RegraNegocioException {

    public EstacaoForaDoRecorteException(String mensagem) {
        super(mensagem);
    }

    /** Mensagem padrão: "A estação X não faz parte do recorte da Linha 3." */
    public static EstacaoForaDoRecorteException daEstacao(String nomeEstacao) {
        return new EstacaoForaDoRecorteException(
                "A estação " + nomeEstacao + " não faz parte do recorte da Linha 3.");
    }
}
