package com.ruan.authguard.service;

import com.ruan.authguard.mapper.UsuarioMapper;
import com.ruan.authguard.repository.UsuarioRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.entity.UsuarioEntity;
import com.ruan.authguard.enums.Role;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private AdminService adminService;

    @Test
    void deveListarUsuariosQuandoExistiremUsuarios() {

        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        UsuarioEntity usuario1 = new UsuarioEntity();
        usuario1.setId(id1);
        usuario1.setNome("Farao");
        usuario1.setEmail("user1@example.com");
        usuario1.setRole(Role.USER);

        UsuarioEntity usuario2 = new UsuarioEntity();
        usuario2.setId(id2);
        usuario2.setNome("Admin");
        usuario2.setEmail("admin@example.com");
        usuario2.setRole(Role.ADMIN);

        when(usuarioRepository.findAll())
                .thenReturn(List.of(usuario1, usuario2));

        UsuarioResponseDTO dto1 = new UsuarioResponseDTO(
                id1,
                "Farao",
                "user1@example.com",
                Role.USER,
                usuario1.getCriadoEm()
        );

        UsuarioResponseDTO dto2 = new UsuarioResponseDTO(
                id2,
                "Admin",
                "admin@example.com",
                Role.ADMIN,
                usuario2.getCriadoEm()
        );

        when(usuarioMapper.paraDTO(usuario1))
                .thenReturn(dto1);

        when(usuarioMapper.paraDTO(usuario2))
                .thenReturn(dto2);

        List<UsuarioResponseDTO> resultado =
                adminService.listarUsuario();

        assertEquals(2, resultado.size());

        assertEquals(dto1, resultado.get(0));
        assertEquals(dto2, resultado.get(1));

    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremUsuarios() {

        when(usuarioRepository.findAll())
                .thenReturn(List.of());

        List<UsuarioResponseDTO> resultado =
                adminService.listarUsuario();

        assertTrue(resultado.isEmpty());
    }
}
