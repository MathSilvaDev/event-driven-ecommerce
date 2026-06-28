package com.matheus.ecommerce.application.sales.cart.dto.response;

public record CartItemResponse(
        Long id,
        Long productId,
        Integer quantity
) { }
