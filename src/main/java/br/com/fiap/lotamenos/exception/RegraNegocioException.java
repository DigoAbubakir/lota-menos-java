package br.com.fiap.lotamenos.exception;

/**
 * Violação de uma regra de negócio → HTTP 422.
 */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
