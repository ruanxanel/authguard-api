package com.ruan.authguard.controller;

import com.ruan.authguard.entity.UsuarioEntity;
import com.ruan.authguard.enums.Role;
import com.ruan.authguard.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.awt.*;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @Test
    void deveRetornar401QuandoNaoEstiverAutenticado() throws Exception {

        mockMvc.perform(get("/api/admin/users"))
                .andExpect(status().isUnauthorized());

    }

    @Test
    void deveRetornar403QuandoUsuarioNaoForAdmin() throws Exception {

        UUID id = UUID.randomUUID();

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNome("Farao");
        usuario.setEmail("user@example.com");
        usuario.setRole(Role.USER);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        mockMvc.perform(get("/api/admin/users")
                .with(jwt().jwt(token -> token
                        .subject(id.toString())
                        .claim("email", "user@example.com")
                )))
        .andExpect(status().isForbidden());
    }

    @Test
    void deveRetornar200QuandoUsuarioForAdmin() throws Exception {

        // Arrange
        UUID id = UUID.randomUUID();

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNome("Admin");
        usuario.setEmail("admin@example.com");
        usuario.setRole(Role.ADMIN);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.findAll())
                .thenReturn(List.of());

        // Act + Assert
        mockMvc.perform(get("/api/admin/users")
                        .with(jwt().jwt(token -> token
                                .subject(id.toString())
                                .claim("email", "admin@example.com")
                        )))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
