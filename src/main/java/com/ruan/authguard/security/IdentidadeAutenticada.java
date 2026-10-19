package com.ruan.authguard.security;

import java.util.UUID;

public record IdentidadeAutenticada(
        UUID id,
        String email
) {}
