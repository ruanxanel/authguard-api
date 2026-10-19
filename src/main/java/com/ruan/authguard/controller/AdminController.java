package com.ruan.authguard.controller;

import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public List<UsuarioResponseDTO> listarUsuarios() {
        return adminService.listarUsuario();
    }
}
