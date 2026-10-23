package com.ruan.authguard.controller;

import com.ruan.authguard.dto.UsuarioRequestDTO;
import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.security.IdentidadeAutenticada;
import com.ruan.authguard.security.UsuarioAutenticado;
import com.ruan.authguard.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "Usuários",
        description = "Gerenciamento do perfil do usuário autenticado"
)
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioAutenticado usuarioAutenticado;

    @Operation(
            summary = "Criar perfil",
            description = "Cria o perfil do usuário autenticado utilizando sua identidade JWT."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "409", description = "Usuário já cadastrado")
    })
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



    @Operation(
            summary = "Consultar meu perfil",
            description = "Retorna os dados do usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
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



    @Operation(
            summary = "Atualizar meu perfil",
            description = "Atualiza o nome do usuário autenticado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "404", description = "Perfil não encontrado")
    })
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
