package com.matheus.ecommerce.application.sales.cart.controller;

import com.matheus.ecommerce.application.sales.cart.dto.request.ChangeItemQuantityRequest;
import com.matheus.ecommerce.application.sales.cart.dto.request.CreateCartItemRequest;
import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemInfoResponse;
import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemResponse;
import com.matheus.ecommerce.application.sales.cart.service.CartService;
import com.matheus.ecommerce.common.security.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<Page<CartItemInfoResponse>> findByUserCart(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize){

        UUID userId = AuthUtils.getUserIdByJwt(jwt);
        return ResponseEntity
                .ok(cartService.findByUserCart(userId, pageNumber, pageSize));
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> addToCart(@AuthenticationPrincipal Jwt jwt,
                                                      @Valid @RequestBody CreateCartItemRequest request){
        UUID userId = AuthUtils.getUserIdByJwt(jwt);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartService.addToCart(userId, request));
    }

    @PostMapping("/change-quantity")
    public ResponseEntity<CartItemInfoResponse> changeQuantityCartItem(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody ChangeItemQuantityRequest request){

        UUID userId = AuthUtils.getUserIdByJwt(jwt);
        return ResponseEntity.ok(
                cartService.changeQuantity(userId, request));
    }

    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable Long cartItemId){
        UUID userId = AuthUtils.getUserIdByJwt(jwt);
        cartService.deleteCartItem(userId, cartItemId);

        return ResponseEntity.noContent().build();
    }
}
