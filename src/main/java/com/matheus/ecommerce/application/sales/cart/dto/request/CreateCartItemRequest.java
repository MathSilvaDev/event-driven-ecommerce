package com.matheus.ecommerce.application.sales.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCartItemRequest(
        @NotNull
        Long productId,

        @NotNull
        @Positive
        Integer quantity
) {}
