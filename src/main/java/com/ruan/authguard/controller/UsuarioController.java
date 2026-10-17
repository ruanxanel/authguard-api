package com.ruan.authguard.controller;

import com.ruan.authguard.dto.UsuarioRequestDTO;
import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.entity.UsuarioEntity;
import com.ruan.authguard.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/profile")
    public ResponseEntity<UsuarioResponseDTO> criarPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UsuarioRequestDTO request
            ) {
        UUID uuid = UUID.fromString(jwt.getSubject());
        String email = jwt.getClaimAsString("email");

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("E-mail não enontrado no token");
        }

        UsuarioEntity usuario = usuarioService.criarPerfil(uuid, request.nome(), email);
        UsuarioResponseDTO response = usuarioService.converterParaDTO(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UsuarioResponseDTO> buscarPerfil(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID uuid = UUID.fromString(jwt.getSubject());
        UsuarioEntity usuario = usuarioService.buscarPorId(uuid);
        UsuarioResponseDTO response = usuarioService.converterParaDTO(usuario);

        return ResponseEntity.ok(response);
    }
}
