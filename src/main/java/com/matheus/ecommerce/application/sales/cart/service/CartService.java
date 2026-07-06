package com.matheus.ecommerce.application.sales.cart.service;

import com.matheus.ecommerce.application.sales.cart.dto.request.ChangeItemQuantityRequest;
import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemInfoResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.catalog.product.repository.ProductRepository;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    public Page<CartItemInfoResponse> findByUserCart(UUID userId, int pageNumber, int pageSize){
        User user = getUserById(userId);

        Pageable pageable = PageRequest.of(
                pageNumber, pageSize,
                Sort.by("product.name").ascending()
        );

        return cartItemRepository.findByCart(user.getCart(), pageable)
                .map(this::toInfoResponse);
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

    @Transactional
    public void deleteCartItem(UUID userId, Long cartItemId){
        User user = getUserById(userId);

        CartItem cartItem = cartItemRepository.findByIdAndCart(cartItemId, user.getCart())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "cartItem not found"));

        user.getCart().removeItem(cartItem);
        cartItemRepository.delete(cartItem);
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
