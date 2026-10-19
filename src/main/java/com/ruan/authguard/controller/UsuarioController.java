package com.ruan.authguard.controller;

import com.ruan.authguard.dto.UsuarioRequestDTO;
import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.security.IdentidadeAutenticada;
import com.ruan.authguard.security.UsuarioAutenticado;
import com.ruan.authguard.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioAutenticado usuarioAutenticado;

    @PostMapping("/profile")
    public ResponseEntity<UsuarioResponseDTO> criarPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UsuarioRequestDTO request
    ) {
        IdentidadeAutenticada identidade =
                usuarioAutenticado.extrair(jwt);

        UsuarioResponseDTO response =
                usuarioService.criarPerfil(identidade, request.nome());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UsuarioResponseDTO> buscarPerfil(
            @AuthenticationPrincipal Jwt jwt
    ) {
        IdentidadeAutenticada identidade =
                usuarioAutenticado.extrair(jwt);

        UsuarioResponseDTO response =
                usuarioService.buscarPerfil(identidade.id());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<UsuarioResponseDTO> atualizarPerfil(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody UsuarioRequestDTO request
    ) {
        IdentidadeAutenticada identidade =
                usuarioAutenticado.extrair(jwt);

        UsuarioResponseDTO response =
                usuarioService.atualizarPerfil(
                        identidade.id(),
                        request.nome()
                );

        return ResponseEntity.ok(response);
    }
}
