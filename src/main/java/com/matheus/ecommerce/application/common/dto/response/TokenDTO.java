package com.matheus.ecommerce.application.common.dto.response;

import java.time.Instant;

public record TokenDTO(
        String token,
        Instant expiresAt
) { }
