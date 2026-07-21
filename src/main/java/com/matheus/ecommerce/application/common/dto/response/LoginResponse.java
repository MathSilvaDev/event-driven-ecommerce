package com.matheus.ecommerce.application.common.dto.response;

public record LoginResponse(
        TokenDTO accessToken,
        TokenDTO refreshToken
) { }
