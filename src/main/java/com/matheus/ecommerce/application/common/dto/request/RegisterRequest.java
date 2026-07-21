package com.matheus.ecommerce.application.common.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank
        @Size(min = 3, max = 24)
        String username,

        @Email
        @NotBlank
        String email,

        @NotBlank
        String password
){}
