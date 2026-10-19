package com.ruan.authguard.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UsuarioAutenticado {

    public IdentidadeAutenticada extrair(Jwt jwt) {

        UUID id;

        try {
            id = UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new IdentidadeInvalidaException(
                    "Identificador inválido no token"
            );
        }

        String email = jwt.getClaimAsString("email");

        if (email == null || email.isBlank()) {
            throw new IdentidadeInvalidaException(
                    "E-mail não encontrado no token"
            );
        }

        return new IdentidadeAutenticada(id, email);
    }
}
