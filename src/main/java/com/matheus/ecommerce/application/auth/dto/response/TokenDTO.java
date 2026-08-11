package com.matheus.ecommerce.application.auth.dto.response;

import java.time.Instant;

public record TokenDTO(
        String token,
        Instant expiresAt
) { }
