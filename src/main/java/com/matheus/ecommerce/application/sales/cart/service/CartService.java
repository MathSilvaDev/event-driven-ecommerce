package com.matheus.ecommerce.application.sales.cart.service;

import com.matheus.ecommerce.application.sales.cart.dto.request.ChangeItemQuantityRequest;
import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemInfoResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import com.matheus.ecommerce.domain.catalog.product.repository.ProductRepository;
import com.matheus.ecommerce.domain.sales.cart.entity.Cart;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import com.matheus.ecommerce.domain.sales.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<CartItemInfoResponse> findByUserCart(UUID userId){
        User user = getUserById(userId);

        return user.getCart().getCartItems()
                .stream()
                .map(this::toInfoResponse)
                .toList();
    }

    @Transactional
    public CartItemInfoResponse changeQuantity(UUID userId, ChangeItemQuantityRequest request){
        User user = getUserById(userId);

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct_Id(user.getCart(), request.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product or CartItem not found"));

        if((cartItem.getQuantity() + request.quantity()) <= 0){
            user.getCart().removeItem(cartItem);
            cartItemRepository.delete(cartItem);

            return null;
        }

        cartItem.addQuantity(request.quantity());

        return toInfoResponse(cartItem);
    }

    private User getUserById(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
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
