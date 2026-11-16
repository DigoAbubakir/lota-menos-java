package br.com.fiap.lotamenos.exception;

public class EstacaoForaDoRecorteException extends RegraNegocioException {
    public EstacaoForaDoRecorteException(String mensagem) {
        super(mensagem);
    }

    public static EstacaoForaDoRecorteException daEstacao(String nomeEstacao) {
        return new EstacaoForaDoRecorteException(
                "A estação " + nomeEstacao + " não faz parte do recorte da Linha 3.");
    }
}
