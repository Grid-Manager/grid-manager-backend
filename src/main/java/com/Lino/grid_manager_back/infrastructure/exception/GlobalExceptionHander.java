package com.Lino.grid_manager_back.infrastructure.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.core.AuthenticationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHander {
    @ExceptionHandler(ResourceNotFoundException.class)
    ProblemDetail handleNotFound(ResourceNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    ProblemDetail handleDuplicate(DuplicateResourceException exception) {
        return problem(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    ProblemDetail handleBadRequest(Exception exception) {
        ProblemDetail problem = problem(HttpStatus.BAD_REQUEST, "Dados de entrada inv\u00e1lidos.");
        if (exception instanceof MethodArgumentNotValidException validationException) {
            Map<String, String> fields = new LinkedHashMap<>();
            validationException.getBindingResult().getFieldErrors()
                    .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));
            problem.setProperty("fields", fields);
        } else {
            problem.setDetail(exception.getMessage());
        }
        return problem;
    }

    @ExceptionHandler(AuthenticationException.class)
    ProblemDetail handleAuthentication(AuthenticationException exception) {
        return problem(HttpStatus.UNAUTHORIZED, "Credenciais inv\u00e1lidas.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ProblemDetail handleDataIntegrity(DataIntegrityViolationException exception) {
        return problem(HttpStatus.CONFLICT, "A opera\u00e7\u00e3o viola um v\u00ednculo ou regra de unicidade do recurso.");
    }

    private ProblemDetail problem(HttpStatus status, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(status.getReasonPhrase());
        return problem;
    }
}
