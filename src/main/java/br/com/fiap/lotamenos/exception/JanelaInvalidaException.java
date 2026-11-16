package br.com.fiap.lotamenos.exception;

/**
 * Janela de horário de saída inválida → HTTP 422.
 */
public class JanelaInvalidaException extends RegraNegocioException {

    public JanelaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
