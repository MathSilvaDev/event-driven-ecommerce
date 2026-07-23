package com.matheus.ecommerce.application.sales.order.service;

import com.matheus.ecommerce.application.common.UtilsTest;
import com.matheus.ecommerce.application.sales.order.dto.response.OrderResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import com.matheus.ecommerce.domain.sales.order.entity.Order;
import com.matheus.ecommerce.domain.sales.order.entity.OrderItem;
import com.matheus.ecommerce.domain.sales.order.repository.OrderItemRepository;
import com.matheus.ecommerce.domain.sales.order.repository.OrderRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

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
        void shouldCreateOrder(){
            User user = UtilsTest.newUser();
            Set<CartItem> cartItems = genCartItems(user, 3, 1);
            user.getCart().addItems(cartItems);
            user.getCart().getCartItems().stream()
                    .toList().getFirst().toggleSelected();

            Mockito.when(userRepository.findById(user.getId()))
                    .thenReturn(Optional.of(user));

            assertEquals(3, user.getCart().getCartItems().size());
            assertEquals(0, user.getOrders().size());

            orderService.createOrder(user.getId());

            assertEquals(1, user.getCart().getCartItems().size());
            assertEquals(1, user.getOrders().size());
            assertEquals(2, user.getOrders().getFirst().getOrderItems().size());

            List<CartItem> cartItemsSelected = cartItems.stream()
                    .filter(CartItem::isSelected)
                    .toList();

            Mockito.verify(orderItemRepository).saveAll(Mockito.anyCollection());
            Mockito.verify(orderRepository).save(Mockito.any(Order.class));
            Mockito.verify(cartItemRepository).deleteAll(cartItemsSelected);

        }

        @Test
        void shouldThrowIfQuantityUnavailable(){
            User user = UtilsTest.newUser();
            Set<CartItem> cartItems = genCartItems(user, 2, 0);
            user.getCart().addItems(cartItems);

            Mockito.when(userRepository.findById(user.getId()))
                    .thenReturn(Optional.of(user));

            assertThrows(ResponseStatusException.class,
                    () -> orderService.createOrder(user.getId()));
        }

        private Set<CartItem> genCartItems(User user, int quantity, int productQuantity){

            Set<CartItem> cartItems = new HashSet<>();

            for (int i = 0; i < quantity; i++){
                CartItem cartItem = new CartItem(
                        user.getCart(),
                        UtilsTest.newProduct(productQuantity),
                        1);

                cartItems.add(cartItem);
            }

            return cartItems;
        }
    }

    @Nested
    class FindAllMyOrders{

        @Test
        void shouldFindAllMyOrder(){
            User user = UtilsTest.newUser();
            Product product = UtilsTest.newProduct(5);
            Order order = new Order(user,
                    List.of(new OrderItem(product, product.getPrice(), 3)));
            Page<Order> pageOrder = new PageImpl<>(List.of(order));

            Mockito.when(userRepository.findById(user.getId()))
                    .thenReturn(Optional.of(user));

            Mockito.when(orderRepository.findByUser(
                    Mockito.eq(user), Mockito.any(Pageable.class)))
                    .thenReturn(pageOrder);

            Page<OrderResponse> response =
                    orderService.findAllMyOrders(user.getId(), 0, 10);

            assertEquals(1 ,response.getSize());

            Mockito.verify(userRepository).findById(user.getId());
            Mockito.verify(orderRepository).findByUser(
                    Mockito.eq(user), Mockito.any(Pageable.class));
        }
    }

    @Nested
    class FindAllOrders{

    }
}