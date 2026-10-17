package com.ruan.authguard.dto;

import java.time.LocalDateTime;

public record ErrorResponse (
     int status,
     String erro,
     String mensagem,
     LocalDateTime timestamp
) {}
