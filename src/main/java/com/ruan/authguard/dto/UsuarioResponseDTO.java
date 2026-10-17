package com.ruan.authguard.dto;

import com.ruan.authguard.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioResponseDTO(
        UUID id,
        String nome,
        String email,
        Role role,
        LocalDateTime criadoEm
) {
}
