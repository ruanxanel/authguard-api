package com.ruan.authguard.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("api/public/health")
    public String health() {
        return "AuthGuard API funcionando";
    }
}
