package com.matheus.ecommerce.application.sales.cart.dto.response;

import java.math.BigDecimal;

public record CartItemInfoResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer quantity
) { }
