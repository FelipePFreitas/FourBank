package com.felipefreitas.FourBank.adapters.in.web.handler;

import com.felipefreitas.FourBank.domain.enums.ErrorEnum;
import com.felipefreitas.FourBank.domain.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Exceções de Regra de Negócio Customizadas (BaseException)
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ProblemDetail> handleBaseException(BaseException ex) {
        ErrorEnum error = ex.getErrorEnum();

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatusCode.valueOf(error.getHttpStatus()),
                error.getErrorMessage()
        );

        problemDetail.setTitle("Erro de Regra de Negócio");
        problemDetail.setType(URI.create("https://conectaclinica.com.br/errors/" + error.name().toLowerCase()));
        problemDetail.setProperty("errorCode", error.getErrorCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(error.getHttpStatus()).body(problemDetail);
    }

    // 2. Erros de Validação dos DTOs (@Valid / @NotBlank / @NotNull / @CPF etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationException(MethodArgumentNotValidException ex) {
        ErrorEnum error = ErrorEnum.DADOS_INVALIDOS;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Um ou mais campos estão inválidos. Faça o preenchimento correto e tente novamente."
        );

        Map<String, String> invalidFields = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Campo inválido",
                        (mensagemAntiga, mensagemNova) -> mensagemAntiga
                ));

        problemDetail.setTitle("Erro de Validação de Dados");
        problemDetail.setType(URI.create("https://conectaclinica.com.br/errors/dados-invalidos"));
        problemDetail.setProperty("errorCode", error.getErrorCode());
        problemDetail.setProperty("invalidFields", invalidFields);
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    // 3. JSON Malformado ou Tipos Incompatíveis na Requisição
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ErrorEnum error = ErrorEnum.DADOS_INVALIDOS;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "O corpo da requisição (JSON) está malformado ou contém tipos de dados inválidos."
        );

        problemDetail.setTitle("Requisição Inválida");
        problemDetail.setType(URI.create("https://conectaclinica.com.br/errors/json-invalido"));
        problemDetail.setProperty("errorCode", error.getErrorCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    // 4. Fallback para Qualquer Erro Não Trato no Servidor (500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUncaughtException(Exception ex) {
        ErrorEnum error = ErrorEnum.ERRO_INTERNO_SERVIDOR;

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                error.getErrorMessage()
        );

        problemDetail.setTitle("Erro Interno do Servidor");
        problemDetail.setType(URI.create("https://conectaclinica.com.br/errors/erro-interno"));
        problemDetail.setProperty("errorCode", error.getErrorCode());
        problemDetail.setProperty("timestamp", Instant.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}