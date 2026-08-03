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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/me")
    public ResponseEntity<Page<OrderResponse>> findMyOrders(
            @AuthenticationPrincipal Jwt jwt,
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

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@AuthenticationPrincipal Jwt jwt){

        UUID userId = AuthUtils.getUserIdByJwt(jwt);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createOrder(userId));
    }

    @PostMapping("/simulate-payment/{id}")
    public ResponseEntity<Void> simulatePayment(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable Long id){
        UUID userId = AuthUtils.getUserIdByJwt(jwt);
        orderService.simulatePayment(userId, id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/simulate-shipment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> simulateShipment(@RequestBody List<Long> orderIds){
        orderService.simulateShipment(orderIds);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/simulate-delivered/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> simulateDelivered(@PathVariable Long id){
        orderService.simulateDelivered(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changePaidOrderToPreparing(@RequestBody List<Long> orderIds){
        orderService.changePaidOrderToPreparing(orderIds);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelOrderIfIsNotPaid(@AuthenticationPrincipal Jwt jwt,
                                                       @PathVariable Long id){
        UUID userId = AuthUtils.getUserIdByJwt(jwt);

        orderService.cancelOrderIfIsNotPaid(userId, id);
        return ResponseEntity.noContent().build();
    }
}
