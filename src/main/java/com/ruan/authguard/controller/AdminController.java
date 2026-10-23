package com.ruan.authguard.controller;

import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@Tag(
        name = "Administração",
        description = "Operações restritas a administradores"
)
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @Operation(
            summary = "Listar usuários",
            description = "Lista os usuários cadastrados. Requer permissão ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @GetMapping("/users")
    public List<UsuarioResponseDTO> listarUsuarios() {
        return adminService.listarUsuario();
    }
}
