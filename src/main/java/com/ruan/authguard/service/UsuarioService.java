package com.ruan.authguard.service;

import com.ruan.authguard.dto.UsuarioRequestDTO;
import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.entity.UsuarioEntity;
import com.ruan.authguard.enums.Role;
import com.ruan.authguard.exception.UsuarioCadastradoException;
import com.ruan.authguard.exception.UsuarioNaoEncontradoException;
import com.ruan.authguard.mapper.UsuarioMapper;
import com.ruan.authguard.repository.UsuarioRepository;
import com.ruan.authguard.security.IdentidadeAutenticada;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPerfil(UUID id) {
        UsuarioEntity usuario = buscarPorId(id);

        return usuarioMapper.paraDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO criarPerfil(
            IdentidadeAutenticada identidade,
            String nome
    ) {
        UUID id = identidade.id();
        String email = identidade.email();

        if (usuarioRepository.existsById(id)) {
            throw new UsuarioCadastradoException(
                    "Usuário já cadastrado"
            );
        }

        if (usuarioRepository.existsByEmail(email)) {
            throw new UsuarioCadastradoException(
                    "E-mail já cadastrado"
            );
        }

        UsuarioEntity usuario = new UsuarioEntity();

        usuario.setId(id);
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setRole(Role.USER);

        UsuarioEntity salvo = usuarioRepository.save(usuario);

        return usuarioMapper.paraDTO(salvo);
    }

    @Transactional
    public UsuarioResponseDTO atualizarPerfil(UUID id, String nome) {
        UsuarioEntity usuario = buscarPorId(id);

        usuario.setNome(nome);

        UsuarioEntity salvo = usuarioRepository.save(usuario);

        return usuarioMapper.paraDTO(salvo);
    }

    private UsuarioEntity buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new UsuarioNaoEncontradoException(
                                "Usuário não encontrado"
                        )
                );
    }
}
