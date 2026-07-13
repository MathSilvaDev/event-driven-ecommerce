package com.matheus.ecommerce.application.sales.cart.dto.request;

import jakarta.validation.constraints.NotNull;

public record ChangeItemQuantityRequest(
        @NotNull
        Long cartItemId,

        @NotNull
        Integer quantity
) { }
