package com.ruan.authguard.exception;

import com.ruan.authguard.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioNaoEncontrado(
            UsuarioNaoEncontradoException ex
    ) {
        ErrorResponse corpo = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpo);
    }

    @ExceptionHandler(UsuarioCadastradoException.class)
    public ResponseEntity<ErrorResponse> handleUsuarioCadastrado(
            UsuarioCadastradoException ex
    ) {
        ErrorResponse corpo = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        FieldError erro = ex.getBindingResult()
                .getFieldErrors()
                .getFirst();

        ErrorResponse corpo = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                erro.getDefaultMessage(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(corpo);
    }
}
