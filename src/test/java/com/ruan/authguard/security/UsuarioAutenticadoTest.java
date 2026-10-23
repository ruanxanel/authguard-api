package com.ruan.authguard.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioAutenticadoTest {

    private final UsuarioAutenticado usuarioAutenticado =
            new UsuarioAutenticado();

    @Test
    void deveLancarExcecaoQuandoUuidForInvalido() {

        Jwt jwt = Jwt.withTokenValue("token-tesste")
                .header("alg", "none")
                .subject("uuid-invalido")
                .claim("email", "user@example.com")
                .build();

        IdentidadeInvalidaException exception = assertThrows(
                IdentidadeInvalidaException.class,
                () -> usuarioAutenticado.extrair(jwt)
        );

        assertEquals(
                "Identificador inválido no token",
                exception.getMessage()
        );
    }

    @Test
    void deveExtrairIdentidadeQuandoTokenForValido() {
        UUID id = UUID.randomUUID();
        String email = "user@example.com";

        Jwt jwt = Jwt.withTokenValue("token-teste")
                .header("alg", "none")
                .subject(id.toString())
                .claim("email", email)
                .build();

        IdentidadeAutenticada resultado =
                usuarioAutenticado.extrair(jwt);

        assertEquals(id, resultado.id());
        assertEquals(email, resultado.email());
    }

    @Test
    void deveLancarExcecaoQuandoEmailNaoExistir() {

        UUID id = UUID.randomUUID();

        Jwt jwt = Jwt.withTokenValue("token-teste")
                .header("alg", "none")
                .subject(id.toString())
                .build();


        IdentidadeInvalidaException exception = assertThrows(
                IdentidadeInvalidaException.class,
                () -> usuarioAutenticado.extrair(jwt)
        );

        assertEquals(
                "E-mail não encontrado no token",
                exception.getMessage()
        );


    }

    @Test
    void deveLancarExcecaoQuandoEmailEstiverVazio() {

        UUID id = UUID.randomUUID();

        Jwt jwt = Jwt.withTokenValue("token-teste")
                .header("alg", "none")
                .subject(id.toString())
                .claim("email", "")
                .build();

        IdentidadeInvalidaException exception = assertThrows(
                IdentidadeInvalidaException.class,
                () -> usuarioAutenticado.extrair(jwt)
        );

        assertEquals(
                "E-mail não encontrado no token",
                exception.getMessage()
        );
    }
}
