package com.matheus.ecommerce.application.sales.order.controller;

import com.matheus.ecommerce.application.sales.order.dto.response.OrderResponse;
import com.matheus.ecommerce.application.sales.order.service.OrderService;
import com.matheus.ecommerce.common.security.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal Jwt jwt){
        UUID userId = AuthUtils.getUserIdByJwt(jwt);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(userId));
    }
}
