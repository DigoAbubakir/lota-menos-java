package br.com.fiap.lotamenos.exception;

/**
 * Antispam: o usuário já relatou a mesma estação há menos de 10 minutos → HTTP 409.
 */
public class RelatoDuplicadoException extends RuntimeException {

    public RelatoDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
