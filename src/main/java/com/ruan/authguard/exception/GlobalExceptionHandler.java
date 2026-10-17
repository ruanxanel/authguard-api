package com.ruan.authguard.exception;

import com.ruan.authguard.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
                404,
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
                409,
                "Conflict",
                ex.getMessage(),
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpo);
    }
}
