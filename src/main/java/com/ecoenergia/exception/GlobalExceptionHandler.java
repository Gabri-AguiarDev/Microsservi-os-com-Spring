package com.ecoenergia.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(RecursoNaoEncontradoException ex, HttpServletRequest req) {
        return montar(HttpStatus.NOT_FOUND, ex.getMessage(), req, null);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResposta> regraNegocio(RegraNegocioException ex, HttpServletRequest req) {
        return montar(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req, null);
    }

    @ExceptionHandler({ConflitoException.class, DataIntegrityViolationException.class})
    public ResponseEntity<ErroResposta> conflito(Exception ex, HttpServletRequest req) {
        String msg = ex instanceof ConflitoException ? ex.getMessage() : "Violação de integridade dos dados";
        return montar(HttpStatus.CONFLICT, msg, req, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(MethodArgumentNotValidException ex, HttpServletRequest req) {
        Map<String, String> campos = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(e -> campos.put(e.getField(), e.getDefaultMessage()));
        return montar(HttpStatus.BAD_REQUEST, "Dados de entrada inválidos", req, campos);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErroResposta> requisicaoInvalida(Exception ex, HttpServletRequest req) {
        return montar(HttpStatus.BAD_REQUEST, "Requisição malformada ou com parâmetro de tipo inválido", req, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResposta> credenciais(BadCredentialsException ex, HttpServletRequest req) {
        return montar(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos", req, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> generico(Exception ex, HttpServletRequest req) {
        log.error("Erro não tratado", ex);
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor", req, null);
    }

    private ResponseEntity<ErroResposta> montar(HttpStatus status, String msg, HttpServletRequest req, Map<String, String> campos) {
        var corpo = new ErroResposta(LocalDateTime.now(), status.value(), status.getReasonPhrase(), msg, req.getRequestURI(), campos);
        return ResponseEntity.status(status).body(corpo);
    }
}
