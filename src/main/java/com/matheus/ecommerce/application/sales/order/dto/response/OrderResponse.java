package com.matheus.ecommerce.application.sales.order.dto.response;

import com.matheus.ecommerce.domain.sales.order.enums.OrderStatus;

import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        List<OrderItemResponse> orderItemResponse,
        Instant createdAt
) { }
