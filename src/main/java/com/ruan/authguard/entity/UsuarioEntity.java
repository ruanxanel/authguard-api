package com.ruan.authguard.entity;

import com.ruan.authguard.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "usuario")
@NoArgsConstructor
@Getter @Setter
public class UsuarioEntity {

    @Id
    private UUID id;

    @Column(name = "nome", nullable = false, length = 100, unique = false)
    private String nome;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    public void prePersist() {
        if (criadoEm == null) {
            criadoEm = LocalDateTime.now();
        }
    }
}