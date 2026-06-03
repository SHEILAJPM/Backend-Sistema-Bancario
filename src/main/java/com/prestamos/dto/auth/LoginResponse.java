package com.prestamos.dto.auth;

public record LoginResponse(
    String token,
    String username,
    String rol,
    long expiresIn
) {}
