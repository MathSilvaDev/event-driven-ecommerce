package com.matheus.ecommerce.application.sales.cart.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangeItemQuantityRequest(
        @NotNull
        Long productId,

        @NotNull
        Integer quantity
) { }
