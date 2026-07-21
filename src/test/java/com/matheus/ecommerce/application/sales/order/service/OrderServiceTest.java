package com.matheus.ecommerce.application.sales.order.service;

import com.matheus.ecommerce.application.common.UtilsTest;
import com.matheus.ecommerce.application.sales.order.dto.response.OrderResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import com.matheus.ecommerce.domain.sales.order.entity.Order;
import com.matheus.ecommerce.domain.sales.order.repository.OrderItemRepository;
import com.matheus.ecommerce.domain.sales.order.repository.OrderRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Nested
    class CreateOrder{

        @Test
        void shouldCreateOrderSuccessfully(){
            User user = UtilsTest.newUser();
            List<CartItem> cartItems = genCartItems(user, 2);
            user.getCart().addItems(cartItems);
            user.getCart().getCartItems().getFirst().toggleSelected();

            Mockito.when(userRepository.findById(user.getId()))
                    .thenReturn(Optional.of(user));

            assertEquals(2, user.getCart().getCartItems().size());

            OrderResponse response = orderService.createOrder(user.getId());

            assertEquals(1, user.getCart().getCartItems().size());

            List<CartItem> cartItemsSelected = cartItems.stream()
                    .filter(CartItem::isSelected)
                    .toList();

            Mockito.verify(orderItemRepository).saveAll(Mockito.anyCollection());
            Mockito.verify(orderRepository).save(Mockito.any(Order.class));
            Mockito.verify(cartItemRepository).deleteAll(cartItemsSelected);

        }

        private List<CartItem> genCartItems(User user, int quantity){

            List<CartItem> cartItems = new ArrayList<>();

            for (int i = 0; i < quantity; i++){
                CartItem cartItem = new CartItem(
                        user.getCart(),
                        UtilsTest.newProduct(),
                        1);

                cartItems.add(cartItem);
            }

            return cartItems;
        }


    }
}