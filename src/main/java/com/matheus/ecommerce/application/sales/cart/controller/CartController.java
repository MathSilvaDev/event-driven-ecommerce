package com.matheus.ecommerce.application.sales.cart.controller;

import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemInfoResponse;
import com.matheus.ecommerce.application.sales.cart.service.CartService;
import com.matheus.ecommerce.common.security.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemInfoResponse>> findByUserCart(@AuthenticationPrincipal Jwt jwt){
        UUID userId = AuthUtils.getUserIdByJwt(jwt);

        return ResponseEntity
                .ok(cartService.findByUserCart(userId));
    }

    //add endpoint to decrease and increase product quantity in cart
    //add endpoint to remove product in cart
}
