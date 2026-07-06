package com.matheus.ecommerce.application.sales.cart.service;

import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemInfoResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.sales.cart.entity.Cart;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import com.matheus.ecommerce.domain.sales.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public List<CartItemInfoResponse> findByUserCart(UUID userId){
        Cart userCart = getCartByUserId(userId);

        return userCart.getCartItems()
                .stream()
                .map(this::toInfoResponse)
                .toList();
    }

    private Cart getCartByUserId(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        return user.getCart();
    }

    private CartItemInfoResponse toInfoResponse(CartItem cartItem){
        return new CartItemInfoResponse(
                cartItem.getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity()
        );
    }
}
