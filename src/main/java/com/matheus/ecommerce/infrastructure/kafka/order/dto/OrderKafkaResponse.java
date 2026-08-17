package com.matheus.ecommerce.infrastructure.kafka.order.dto;

import com.matheus.ecommerce.domain.sales.order.enums.OrderStatus;

public record OrderKafkaResponse(
    Long orderId,
    OrderStatus orderStatus
) {}
