package com.matheus.ecommerce.application.common.dto.response;

public record RefreshResponse(
        TokenDTO newAccessToken
) { }
