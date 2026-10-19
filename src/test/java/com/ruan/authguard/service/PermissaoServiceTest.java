package com.ruan.authguard.service;

import com.ruan.authguard.entity.UsuarioEntity;
import com.ruan.authguard.enums.Role;
import com.ruan.authguard.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PermissaoServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private PermissaoService permissaoService;

    @Test
    void deveRetornarTrueQuandoUsuarioForAdmin() {

        UUID id = UUID.randomUUID();

        UsuarioEntity usuario = new UsuarioEntity();

        usuario.setId(id);
        usuario.setRole(Role.ADMIN);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        boolean resultado = permissaoService.isAdmin(id);

        System.out.println("Resultado ADMIN: " + resultado);

        assertTrue(resultado);
    }

    @Test
    void deveRetornarFalseQuandoUsuarioForUser() {
        UUID id = UUID.randomUUID();

        UsuarioEntity usuario = new UsuarioEntity();

        usuario.setId(id);
        usuario.setRole(Role.USER);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        boolean resultado = permissaoService.isAdmin(id);

        System.out.println("Resultado USER: " + resultado);

        assertFalse(resultado);
    }

    @Test
    void deveRetornarFalseQuandoUsuarioNaoExistir() {
        UUID id = UUID.randomUUID();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        boolean resultado = permissaoService.isAdmin(id);

        System.out.println("Resultado usuário inexistente: " + resultado);

        assertFalse(resultado);
    }



    @Test
    void deveRetornarTrueQuandoUsuarioForUser() {
        UUID id = UUID.randomUUID();

        UsuarioEntity usuario = new UsuarioEntity();

        usuario.setId(id);
        usuario.setRole(Role.USER);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        boolean resultado = permissaoService.isUser(id);

        System.out.println("Resultado USER: " + resultado);
        assertTrue(resultado);
    }

    @Test
    void deveRetornarFalseQuandoUsuarioForAdmin() {
        UUID id = UUID.randomUUID();

        UsuarioEntity usuario = new UsuarioEntity();

        usuario.setId(id);
        usuario.setRole(Role.ADMIN);

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        boolean resultado = permissaoService.isUser(id);

        System.out.println("Resultado ADMIN: " + resultado);

        assertFalse(resultado);
    }

    @Test
    void deveRetornarFalseQuandoUsuarioNaoExistirNoIsUser() {
        UUID id = UUID.randomUUID();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        boolean resultado = permissaoService.isUser(id);

        System.out.println("Resultado usuarioUser inexistente: " + resultado);

        assertFalse(resultado);
    }

}
