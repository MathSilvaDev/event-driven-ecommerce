package com.matheus.ecommerce.application.sales.order.dto.response;

public record OrderItemResponse(
        Long id,
        String name,
        Integer quantity
) { }
