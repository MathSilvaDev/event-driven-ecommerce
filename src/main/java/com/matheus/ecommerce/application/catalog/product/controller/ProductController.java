package com.matheus.ecommerce.application.catalog.product.controller;

import com.matheus.ecommerce.application.catalog.product.dto.request.CreateProductRequest;
import com.matheus.ecommerce.application.catalog.product.dto.request.EditProductRequest;
import com.matheus.ecommerce.application.catalog.product.dto.response.ProductResponse;
import com.matheus.ecommerce.application.catalog.product.service.ProductService;
import com.matheus.ecommerce.application.sales.cart.dto.request.CreateCartItemRequest;
import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemResponse;
import com.matheus.ecommerce.common.security.AuthUtils;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import jakarta.validation.Valid;
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
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<ProductResponse>> findAll(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize){

        return ResponseEntity
                .ok(productService.findAll(pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id){
        return ResponseEntity
                .ok(productService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody CreateProductRequest request){

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.create(request));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> edit(@PathVariable Long id,
                                     @Valid @RequestBody EditProductRequest request){
        productService.edit(id,request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cart/add")
    public ResponseEntity<CartItemResponse> addToCart(@AuthenticationPrincipal Jwt jwt,
                                                      @Valid @RequestBody CreateCartItemRequest request){
        UUID userId = AuthUtils.getUserIdByJwt(jwt);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.addToCart(userId, request));
    }
}
