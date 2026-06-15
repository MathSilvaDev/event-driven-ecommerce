package com.matheus.ecommerce.application.auth.dto.response;

public record LoginResponse(
        TokenDTO accessToken,
        TokenDTO refreshToken
) { }
