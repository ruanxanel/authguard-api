package com.ruan.authguard.controller;

import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.enums.Role;
import com.ruan.authguard.security.IdentidadeAutenticada;
import com.ruan.authguard.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.ruan.authguard.exception.UsuarioNaoEncontradoException;
import com.ruan.authguard.exception.UsuarioCadastradoException;


import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    void deveRetornar401QuandoUsuarioNaoEstiverAutenticado() throws Exception {

        mockMvc.perform(get("/api/user/profile"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveCriarPerfilQuandoUsuarioEstiverAutenticado() throws Exception {

        UUID id = UUID.randomUUID();
        String email = "user@example.com";
        String nome = "Farao";

        IdentidadeAutenticada identidade =
                new IdentidadeAutenticada(id, email);

        UsuarioResponseDTO response = new UsuarioResponseDTO(
                id,
                nome,
                email,
                Role.USER,
                null
        );

        when(usuarioService.criarPerfil(eq(identidade), eq(nome)))
                .thenReturn(response);

        mockMvc.perform(post("/api/user/profile")
                .with(jwt().jwt(token -> token
                        .subject(id.toString())
                        .claim("email", email)
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "nome": "Farao"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value(nome))
                .andExpect(jsonPath("$.email").value(email));

        verify(usuarioService).criarPerfil(
                eq(identidade),
                eq(nome)
        );
    }

    @Test
    void deveRetornar400QuandoNomeForInvalido() throws Exception {

        UUID id = UUID.randomUUID();
        String email = "user@example.com";

        mockMvc.perform(post("/api/user/profile")
                .with(jwt().jwt(token -> token
                        .subject(id.toString())
                        .claim("email", email)
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "nome": ""
                        }
                        """))
        .andExpect(status().isBadRequest());

        verifyNoInteractions(usuarioService);
    }

    @Test
    void deveRetornar200QuandoBuscarPerfilAutenticado() throws Exception {

        UUID id = UUID.randomUUID();
        String email = "user@example.com";
        String nome = "Farao";

        UsuarioResponseDTO response = new UsuarioResponseDTO(
                id,
                nome,
                email,
                Role.USER,
                null
        );

        when(usuarioService.buscarPerfil(id))
                .thenReturn(response);

        mockMvc.perform(get("/api/user/profile")
                .with(jwt().jwt(token -> token
                        .subject(id.toString())
                        .claim("email", email)
                )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value(nome))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.role").value("USER"));


        verify(usuarioService).buscarPerfil(id);
    }

    @Test
    void deveAtualizarPerfilQuandoUsuarioEstiverAutenticado() throws Exception {

        UUID id = UUID.randomUUID();
        String email = "user@example.com";
        String novoNome = "ruanxanel";

        UsuarioResponseDTO response = new UsuarioResponseDTO(
                id,
                novoNome,
                email,
                Role.USER,
                null
        );

        when(usuarioService.atualizarPerfil(eq(id), eq(novoNome)))
                .thenReturn(response);

        mockMvc.perform(put("/api/user/profile")
                .with(jwt().jwt(token -> token
                        .subject(id.toString())
                        .claim("email", email)
                ))
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "nome": "ruanxanel"
                        }
                        """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id.toString()))
        .andExpect(jsonPath("$.nome").value(novoNome))
        .andExpect(jsonPath("$.email").value(email))
        .andExpect(jsonPath("$.role").value("USER"));

        verify(usuarioService).atualizarPerfil(
                eq(id),
                eq(novoNome)
        );
    }

    @Test
    void deveRetornar400QuandoAtualizarPerfilComNomeInvalido() throws Exception {

        UUID id = UUID.randomUUID();
        String email = "user@example.com";
        String novoNome = "";

        UsuarioResponseDTO response = new UsuarioResponseDTO(
                id,
                novoNome,
                email,
                Role.USER,
                null
        );

        when(usuarioService.atualizarPerfil(eq(id), eq(novoNome)))
                .thenReturn(response);

        mockMvc.perform(put("/api/user/profile")
                        .with(jwt().jwt(token -> token
                                .subject(id.toString())
                                .claim("email", email)
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.erro").exists())
                .andExpect(jsonPath("$.mensagem").exists())
                .andExpect(jsonPath("$.timestamp").exists());


        verifyNoInteractions(usuarioService);
    }

    @Test
    void deveRetornar404QuandoUsuarioNaoForEncontrado() throws Exception {

        UUID id = UUID.randomUUID();
        String email = "user@example.com";


        when(usuarioService.buscarPerfil(id))
                .thenThrow(new UsuarioNaoEncontradoException("Usuário não encontrado"));

        mockMvc.perform(get("/api/user/profile")
                        .with(jwt().jwt(token -> token
                                .subject(id.toString())
                                .claim("email", email)
                        )))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensagem").value("Usuário não encontrado"));

        verify(usuarioService).buscarPerfil(id);
    }

    @Test
    void deveRetornar409QuandoUsuarioJaEstiverCadastrado() throws Exception {

        UUID id = UUID.randomUUID();
        String email = "user@example.com";
        String nome = "Farao";

        IdentidadeAutenticada identidade =
                new IdentidadeAutenticada(id, email);

        when(usuarioService.criarPerfil(eq(identidade), eq(nome)))
                .thenThrow(new UsuarioCadastradoException("Usuário já cadastrado"));

        mockMvc.perform(post("/api/user/profile")
                        .with(jwt().jwt(token -> token
                                .subject(id.toString())
                                .claim("email", email)
                        ))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "nome": "Farao"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensagem").value("Usuário já cadastrado"));

        verify(usuarioService).criarPerfil(eq(identidade), eq(nome));
    }
}
