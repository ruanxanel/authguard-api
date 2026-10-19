package com.ruan.authguard.service;

import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.entity.UsuarioEntity;
import com.ruan.authguard.enums.Role;
import com.ruan.authguard.exception.UsuarioCadastradoException;
import com.ruan.authguard.exception.UsuarioNaoEncontradoException;
import com.ruan.authguard.mapper.UsuarioMapper;
import com.ruan.authguard.repository.UsuarioRepository;

import com.ruan.authguard.security.IdentidadeAutenticada;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.buscarPerfil(id)
        );

        assertEquals(
                "Usuário não encontrado",
                exception.getMessage()
        );
    }

    @Test
    void deveRetornarPerfilQuandoUsuarioExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNome("Farao");
        usuario.setEmail("user@example.com");
        usuario.setRole(Role.USER);

        UsuarioResponseDTO responseEsperada = new UsuarioResponseDTO(
                id,
                "Farao",
                "user@example.com",
                Role.USER,
                usuario.getCriadoEm()
        );

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(usuarioMapper.paraDTO(usuario))
                .thenReturn(responseEsperada);

        // Act
        UsuarioResponseDTO resultado = usuarioService.buscarPerfil(id);

        // Assert
        assertEquals(responseEsperada, resultado);
    }

    @Test
    void deveCriarPerfilQuandoDadosForemValidos() {

        UUID id = UUID.randomUUID();

        IdentidadeAutenticada identidade =
                new IdentidadeAutenticada(id, "user@example.com");

        String nome = "Farao";

        when(usuarioRepository.existsById(id))
                .thenReturn(false);

        when(usuarioRepository.existsByEmail(identidade.email()))
                .thenReturn(false);

        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(usuarioMapper.paraDTO(any(UsuarioEntity.class)))
                .thenAnswer(invocation -> {

                    UsuarioEntity usuario = invocation.getArgument(0);

                    return new UsuarioResponseDTO(
                            usuario.getId(),
                            usuario.getNome(),
                            usuario.getEmail(),
                            usuario.getRole(),
                            usuario.getCriadoEm()
                    );
                });

        UsuarioResponseDTO resultado =
                usuarioService.criarPerfil(identidade, nome);

        assertEquals(id, resultado.id());
        assertEquals(nome, resultado.nome());
        assertEquals(identidade.email(), resultado.email());
        assertEquals(Role.USER, resultado.role());

        verify(usuarioRepository).save(any(UsuarioEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoUuidJaEstiverCadastrado() {
         UUID id = UUID.randomUUID();

         IdentidadeAutenticada identidade =
                 new IdentidadeAutenticada(id, "user@example.com");

         String nome = "Farao";

         when(usuarioRepository.existsById(id))
                 .thenReturn(true);

        UsuarioCadastradoException exception = assertThrows(
                UsuarioCadastradoException.class,
                () -> usuarioService.criarPerfil(identidade, nome)
        );

        verify(usuarioRepository, never())
                .save(any(UsuarioEntity.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstiverCadastrado() {
        UUID id = UUID.randomUUID();

        IdentidadeAutenticada identidade =
                new IdentidadeAutenticada(id, "user@example.com");

        String nome = "Farao";

        when(usuarioRepository.existsByEmail(identidade.email()))
                .thenReturn(true);

        UsuarioCadastradoException exception = assertThrows(
                UsuarioCadastradoException.class,
                () -> usuarioService.criarPerfil(identidade, nome)
        );

        assertEquals(
                "E-mail já cadastrado",
                exception.getMessage()
        );

        verify(usuarioRepository, never())
                .save(any(UsuarioEntity.class));
    }

    @Test
    void deveAtualizarPerfilQuandoUsuarioExistir() {

        // Arrange
        UUID id = UUID.randomUUID();

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNome("Farao");
        usuario.setEmail("user@example.com");
        usuario.setRole(Role.USER);

        String novoNome = "Ruan Henrique";

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(any(UsuarioEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(usuarioMapper.paraDTO(any(UsuarioEntity.class)))
                .thenAnswer(invocation -> {
                    UsuarioEntity entidade = invocation.getArgument(0);

                    return new UsuarioResponseDTO(
                            entidade.getId(),
                            entidade.getNome(),
                            entidade.getEmail(),
                            entidade.getRole(),
                            entidade.getCriadoEm()
                    );
                });

        // Act
        UsuarioResponseDTO resultado =
                usuarioService.atualizarPerfil(id, novoNome);

        // Assert
        ArgumentCaptor<UsuarioEntity> captor =
                ArgumentCaptor.forClass(UsuarioEntity.class);

        verify(usuarioRepository).save(captor.capture());

        UsuarioEntity usuarioSalvo = captor.getValue();

        assertEquals(novoNome, usuarioSalvo.getNome());
        assertEquals(id, usuarioSalvo.getId());
        assertEquals("user@example.com", usuarioSalvo.getEmail());
        assertEquals(Role.USER, usuarioSalvo.getRole());
    }

    @Test
    void deveLancarExcecaoQuandoAtualizarUsuarioInexistente() {

        // Arrange
        UUID id = UUID.randomUUID();
        String novoNome = "Novo Nome";

        when(usuarioRepository.findById(id))
                .thenReturn(Optional.empty());

        // Act + Assert
        UsuarioNaoEncontradoException exception = assertThrows(
                UsuarioNaoEncontradoException.class,
                () -> usuarioService.atualizarPerfil(id, novoNome)
        );

        assertEquals(
                "Usuário não encontrado",
                exception.getMessage()
        );

        verify(usuarioRepository, never())
                .save(any(UsuarioEntity.class));
    }
}
