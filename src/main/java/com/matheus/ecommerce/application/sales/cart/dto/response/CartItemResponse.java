package com.matheus.ecommerce.application.sales.cart.dto.response;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long productId,
        String name,
        BigDecimal price,
        Integer quantity,
        boolean isSelected
) { }
