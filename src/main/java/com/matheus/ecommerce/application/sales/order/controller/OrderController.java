package com.matheus.ecommerce.application.sales.order.controller;

import com.matheus.ecommerce.application.sales.order.dto.response.OrderResponse;
import com.matheus.ecommerce.application.sales.order.service.OrderService;
import com.matheus.ecommerce.common.security.AuthUtils;
import com.matheus.ecommerce.domain.sales.order.enums.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/me")
    public ResponseEntity<Page<OrderResponse>> findMyOrders(@AuthenticationPrincipal Jwt jwt,
                                                            @RequestParam(defaultValue = "0") int pageNumber,
                                                            @RequestParam(defaultValue = "10") int pageSize){

        UUID userId = AuthUtils.getUserIdByJwt(jwt);

        return ResponseEntity
                .ok(orderService.findMyOrders(userId, pageNumber, pageSize));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<OrderResponse>> findOrders(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) OrderStatus orderStatus){

        return ResponseEntity
                .ok(orderService.findOrders(pageNumber, pageSize, orderStatus));
    }
}
