package com.matheus.ecommerce.application.sales.cart.service;

import com.matheus.ecommerce.application.sales.cart.dto.request.ChangeItemQuantityRequest;
import com.matheus.ecommerce.application.sales.cart.dto.request.CreateCartItemRequest;
import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemInfoResponse;
import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import com.matheus.ecommerce.domain.catalog.product.repository.ProductRepository;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import com.matheus.ecommerce.infrastructure.exception.auth.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

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
    public CartItemResponse addToCart(UUID userId, CreateCartItemRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Product product = getProductById(request.productId());

        Optional<CartItem> cartItemOpt =
                cartItemRepository.findByCartAndProduct(
                        user.getCart(),
                        product
                );

        CartItem cartItem;
        if(cartItemOpt.isPresent()){
            cartItem = cartItemOpt.get();
            cartItem.addQuantity(request.quantity());
        } else{
            cartItem = new CartItem(
                    user.getCart(),
                    product,
                    request.quantity()
            );
        }

        cartItemRepository.save(cartItem);

        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getQuantity()
        );
    }

    @Transactional
    public void changeQuantity(UUID userId, ChangeItemQuantityRequest request){
        User user = getUserById(userId);

        CartItem cartItem = cartItemRepository
                .findByCartAndProduct_Id(user.getCart(), request.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product or CartItem not found"));

        if((cartItem.getQuantity() + request.quantity()) <= 0){
            user.getCart().removeItem(cartItem);
            cartItemRepository.delete(cartItem);

            return;
        }

        cartItem.addQuantity(request.quantity());
    }

    @Transactional
    public void toggleSelected(UUID userId, Long cartItemId){
        User user = getUserById(userId);

        CartItem cartItem = cartItemRepository.findByIdAndCart(cartItemId, user.getCart())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "cartItem not found"));

        cartItem.toggleSelected();
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
                .orElseThrow(UserNotFoundException::new);
    }

    private CartItemInfoResponse toInfoResponse(CartItem cartItem){
        return new CartItemInfoResponse(
                cartItem.getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity()
        );
    }

    private Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));
    }
}
