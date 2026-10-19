package com.ruan.authguard.mapper;

import com.ruan.authguard.dto.UsuarioResponseDTO;
import com.ruan.authguard.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponseDTO paraDTO(UsuarioEntity usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole(),
                usuario.getCriadoEm()
        );
    }
}
