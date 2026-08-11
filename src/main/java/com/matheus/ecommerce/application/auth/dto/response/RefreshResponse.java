package com.matheus.ecommerce.application.auth.dto.response;

public record RefreshResponse(
        TokenDTO newAccessToken
) { }
