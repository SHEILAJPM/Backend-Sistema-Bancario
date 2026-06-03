package com.prestamos.dto.auth;

import java.time.LocalDateTime;

public record LoginAuditResponse(
    String username,
    String rol,
    LocalDateTime timestamp,
    boolean exitoso
) {}
