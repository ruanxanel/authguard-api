package com.ruan.authguard.security;

import tools.jackson.databind.ObjectMapper;
import com.ruan.authguard.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException {

        ErrorResponse corpo = new ErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "Você não possui permissão para acessar este recurso",
                LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS)
        );

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(
                objectMapper.writeValueAsString(corpo)
        );
    }
}