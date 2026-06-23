package com.matheus.ecommerce.application.catalog.product.dto.request;

import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record EditProductRequest(
        String name,
        String description,

        @PositiveOrZero
        BigDecimal price,

        @PositiveOrZero
        Integer quantity
) { }
