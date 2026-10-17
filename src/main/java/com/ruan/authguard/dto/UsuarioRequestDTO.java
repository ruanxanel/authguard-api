package com.ruan.authguard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        @Size(min = 3, max = 100,
                message = "O nome deve conter entre 3 e 100 caracteres")
        String nome
) {
}
