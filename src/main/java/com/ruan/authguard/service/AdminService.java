package com.ruan.authguard.service;

import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.entity.UsuarioEntity;
import com.ruan.authguard.mapper.UsuarioMapper;
import com.ruan.authguard.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @PreAuthorize("@permissaoService.isAdmin(T(java.util.UUID).fromString(authentication.name))")
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarUsuario() {

        List<UsuarioEntity> usuarios = usuarioRepository.findAll();

        return usuarios.stream()
                .map(usuarioMapper::paraDTO)
                .toList();
    }
}
