package com.matheus.ecommerce.application.sales.cart.controller;

import com.matheus.ecommerce.application.sales.cart.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    //add endpoint to decrease product quantity in cart
    //add endpoint to remove product in cart
}
