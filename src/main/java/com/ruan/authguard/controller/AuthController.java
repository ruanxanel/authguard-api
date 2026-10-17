package com.ruan.authguard.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @GetMapping("/api/user/me")
    public String me(@AuthenticationPrincipal Jwt jwt) {
        return "Usuario autenticado: " + jwt.getSubject();
    }
}
