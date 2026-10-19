package com.ruan.authguard.service;

import com.ruan.authguard.enums.Role;
import com.ruan.authguard.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissaoService {

    private final UsuarioRepository usuarioRepository;

    public boolean isAdmin(UUID id) {
        return usuarioRepository.findById(id)
                .map(usuario -> usuario.getRole() == Role.ADMIN)
                .orElse(false);
    }

    public boolean isUser(UUID id) {
        return usuarioRepository.findById(id)
                .map(usuario -> usuario.getRole() == Role.USER)
                .orElse(false);
    }
}
