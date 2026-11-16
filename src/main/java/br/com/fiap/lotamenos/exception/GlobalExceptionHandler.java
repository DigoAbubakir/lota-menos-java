package br.com.fiap.lotamenos.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

/**
 * Tratamento centralizado de exceções: toda resposta de erro sai no formato {@link ErroResponse},
 * com mensagem em português e sem stack trace.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> naoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex.getMessage(), req, List.of());
    }

    /** Também captura EstacaoForaDoRecorteException e JanelaInvalidaException (subclasses). */
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> regraNegocio(RegraNegocioException ex, HttpServletRequest req) {
        return responder(HttpStatus.UNPROCESSABLE_ENTITY, "Regra de negócio", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(RelatoDuplicadoException.class)
    public ResponseEntity<ErroResponse> relatoDuplicado(RelatoDuplicadoException ex, HttpServletRequest req) {
        return responder(HttpStatus.CONFLICT, "Relato duplicado", ex.getMessage(), req, List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> validacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ErroResponse.CampoInvalido> campos = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErroResponse.CampoInvalido(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return responder(HttpStatus.BAD_REQUEST, "Dados inválidos",
                "Um ou mais campos estão inválidos. Verifique a lista 'campos'.", req, campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> corpoInvalido(HttpMessageNotReadableException ex, HttpServletRequest req) {
        String mensagem = "O corpo da requisição está ausente ou mal formatado. "
                + "Verifique o JSON, datas no formato yyyy-MM-dd e valores permitidos.";
        if (ex.getCause() instanceof InvalidFormatException ife) {
            String campo = ife.getPath().isEmpty() ? "?" : ife.getPath().get(ife.getPath().size() - 1).getFieldName();
            mensagem = "Valor '" + ife.getValue() + "' inválido para o campo '" + campo + "'.";
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                mensagem += " Valores aceitos: " + Arrays.toString(ife.getTargetType().getEnumConstants()) + ".";
            }
        }
        return responder(HttpStatus.BAD_REQUEST, "Requisição inválida", mensagem, req, List.of());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tipoInvalido(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String mensagem = "O parâmetro '" + ex.getName() + "' recebeu o valor '" + ex.getValue() + "', que é inválido.";
        Class<?> tipo = ex.getRequiredType();
        if (tipo != null && tipo.isEnum()) {
            mensagem += " Valores aceitos: " + Arrays.toString(tipo.getEnumConstants()) + ".";
        }
        return responder(HttpStatus.BAD_REQUEST, "Requisição inválida", mensagem, req, List.of());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> parametroAusente(MissingServletRequestParameterException ex,
                                                         HttpServletRequest req) {
        return responder(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O parâmetro obrigatório '" + ex.getParameterName() + "' não foi informado.", req, List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResponse> integridade(DataIntegrityViolationException ex, HttpServletRequest req) {
        log.warn("Violação de integridade em {}: {}", req.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT, "Conflito de dados",
                "Operação não permitida: o registro está vinculado a outros dados "
                        + "(ex.: estação com relatos) ou viola uma restrição de unicidade.", req, List.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> rotaInexistente(NoResourceFoundException ex, HttpServletRequest req) {
        return responder(HttpStatus.NOT_FOUND, "Recurso não encontrado",
                "O caminho " + req.getRequestURI() + " não existe. Consulte a documentação em /swagger-ui.html.",
                req, List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> metodoNaoSuportado(HttpRequestMethodNotSupportedException ex,
                                                           HttpServletRequest req) {
        return responder(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido",
                "O método " + ex.getMethod() + " não é suportado neste caminho.", req, List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroResponse> midiaNaoSuportada(HttpMediaTypeNotSupportedException ex,
                                                          HttpServletRequest req) {
        return responder(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de conteúdo não suportado",
                "Envie o corpo como JSON (Content-Type: application/json).", req, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> erroInesperado(Exception ex, HttpServletRequest req) {
        log.error("Erro inesperado em {} {}", req.getMethod(), req.getRequestURI(), ex);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente em instantes.", req, List.of());
    }

    private ResponseEntity<ErroResponse> responder(HttpStatus status, String erro, String mensagem,
                                                   HttpServletRequest req,
                                                   List<ErroResponse.CampoInvalido> campos) {
        ErroResponse corpo = new ErroResponse(
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS),
                status.value(), erro, mensagem, req.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
