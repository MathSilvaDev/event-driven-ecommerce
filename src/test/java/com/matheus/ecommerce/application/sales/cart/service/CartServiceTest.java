package com.matheus.ecommerce.application.sales.cart.service;

import com.matheus.ecommerce.application.sales.cart.dto.request.CreateCartItemRequest;
import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemResponse;
import com.matheus.ecommerce.domain.auth.entity.Role;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.enums.RoleName;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import com.matheus.ecommerce.domain.catalog.product.repository.ProductRepository;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import com.matheus.ecommerce.infrastructure.exception.auth.UserNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    @Nested
    class FindAll{

    }

    @Nested
    class AddToCart{

        @Test
        void shouldThrowIfUserDoesNotExist(){
            UUID userId = UUID.randomUUID();

            CreateCartItemRequest request =
                    new CreateCartItemRequest(1L, 1);

            Mockito.when(userRepository.findById(userId))
                    .thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> cartService.addToCart(userId, request));

            Mockito.verify(userRepository).findById(userId);
        }

        @Test
        void shouldThrowIfProductDoesNotExist(){
            User user = newUser();
            UUID userId = user.getId();
            Long productId = 1L;

            CreateCartItemRequest request =
                    new CreateCartItemRequest(productId, 1);

            Mockito.when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            Mockito.when(productRepository.findById(productId))
                            .thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class,
                    () -> cartService.addToCart(userId, request));

            Mockito.verify(userRepository).findById(userId);
            Mockito.verify(productRepository).findById(productId);
        }

        @Test
        void shouldCreateIfCartItemDoesNotExist(){
            User user = newUser();
            Product product = newProduct();

            UUID userId = user.getId();
            Long productId = product.getId();

            CreateCartItemRequest request =
                    new CreateCartItemRequest(productId, 3);

            Mockito.when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            Mockito.when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            Mockito.when(cartItemRepository.findByCartAndProduct(user.getCart(), product))
                    .thenReturn(Optional.empty());

            CartItemResponse response = cartService.addToCart(userId, request);

            assertEquals(request.productId(), response.productId());
            assertEquals(request.quantity(), response.quantity());

            Mockito.verify(userRepository).findById(userId);
            Mockito.verify(productRepository).findById(productId);
            Mockito.verify(cartItemRepository).findByCartAndProduct(user.getCart(), product);
            Mockito.verify(cartItemRepository).save(any(CartItem.class));
        }

        @Test
        void shouldUpdateIfItemCartExists(){
            User user = newUser();
            Product product = newProduct();
            CartItem cartItem = new CartItem(
                    user.getCart(), product, 2);

            UUID userId = user.getId();
            Long productId = product.getId();

            int oldItemCartQuantity = cartItem.getQuantity();

            CreateCartItemRequest request =
                    new CreateCartItemRequest(productId, 3);

            Mockito.when(userRepository.findById(userId))
                    .thenReturn(Optional.of(user));

            Mockito.when(productRepository.findById(productId))
                    .thenReturn(Optional.of(product));

            Mockito.when(cartItemRepository.findByCartAndProduct(user.getCart(), product))
                    .thenReturn(Optional.of(cartItem));

            CartItemResponse response = cartService.addToCart(userId, request);

            assertEquals(request.productId(), response.productId());
            assertEquals((oldItemCartQuantity + request.quantity()), response.quantity());

            Mockito.verify(userRepository).findById(userId);
            Mockito.verify(productRepository).findById(productId);
            Mockito.verify(cartItemRepository).findByCartAndProduct(user.getCart(), product);
            Mockito.verify(cartItemRepository).save(cartItem);
        }

    }

    private User newUser(){
        User user = new User(
                "user_name",
                "email@email.com",
                "raw_password"
        );
        Role role = new Role(RoleName.BASIC);
        user.addRoles(List.of(role));

        return user;
    }

    private Product newProduct(){
        return new Product(
                "product_name",
                "product_description",
                BigDecimal.TEN,
                5
        );
    }

}