package com.ruan.authguard.service;

import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.entity.UsuarioEntity;
import com.ruan.authguard.enums.Role;
import com.ruan.authguard.exception.UsuarioCadastradoException;
import com.ruan.authguard.exception.UsuarioNaoEncontradoException;
import com.ruan.authguard.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioEntity buscarPorId(UUID id) {

        return usuarioRepository.findById(id)
               .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuario não encontrado"));
    }

    public UsuarioEntity criarPerfil(UUID id, String nome, String email) {

        if (usuarioRepository.existsById(id)) {
            throw new UsuarioCadastradoException("Usuário já cadastrado");
        }
        if (usuarioRepository.existsByEmail(email)) {
            throw new UsuarioCadastradoException("E-mail já cadastrado");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setRole(Role.USER);

        return usuarioRepository.save(usuario);
    }

    public UsuarioResponseDTO converterParaDTO(UsuarioEntity usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getCriadoEm()
        );
    }

    public UsuarioEntity atualizarPerfil(UUID id, String nome) {
        UsuarioEntity usuario = buscarPorId(id);

        usuario.setNome(nome);

        usuarioRepository.save(usuario);

        return usuario;
    }
}
