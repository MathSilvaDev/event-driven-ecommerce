package com.matheus.ecommerce.application.sales.order.dto.response;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        String name,
        BigDecimal price,
        Integer quantity,
        BigDecimal totalValue
) { }
