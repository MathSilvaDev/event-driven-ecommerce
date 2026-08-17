package com.matheus.ecommerce.application.sales.order.service;

import com.matheus.ecommerce.application.auth.UtilsTest;
import com.matheus.ecommerce.application.sales.order.dto.response.OrderResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import com.matheus.ecommerce.domain.sales.order.entity.Order;
import com.matheus.ecommerce.domain.sales.order.entity.OrderItem;
import com.matheus.ecommerce.domain.sales.order.enums.OrderStatus;
import com.matheus.ecommerce.domain.sales.order.repository.OrderItemRepository;
import com.matheus.ecommerce.domain.sales.order.repository.OrderRepository;
import com.matheus.ecommerce.infrastructure.kafka.order.dto.OrderKafkaResponse;
import com.matheus.ecommerce.infrastructure.kafka.order.producer.OrderProducer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.exceptions.misusing.PotentialStubbingProblem;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
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
    @Mock
    private OrderProducer orderProducer;

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

            OrderResponse response = orderService.createOrder(user.getId());

            assertEquals(1, user.getCart().getCartItems().size());
            assertEquals(1, user.getOrders().size());
            assertEquals(2, user.getOrders().getFirst().getOrderItems().size());
            assertEquals(BigDecimal.valueOf(20), response.totalValue());

            List<CartItem> cartItemsSelected = cartItems.stream()
                    .filter(CartItem::isSelected)
                    .toList();

            Mockito.verify(orderItemRepository).saveAll(Mockito.anyCollection());
            Mockito.verify(orderRepository).save(Mockito.any(Order.class));
            Mockito.verify(cartItemRepository).deleteAll(cartItemsSelected);

            Mockito.verify(orderProducer)
                    .sendOrderStatus(
                        new OrderKafkaResponse(
                                response.id(), 
                                response.status())
                    );

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
    class FindMyOrders{

        @Test
        void shouldFindMyOrders(){
            User user = UtilsTest.newUser();
            Product product = UtilsTest.newProduct(5);
            Order order = new Order(user);
            OrderItem orderItem =
                    new OrderItem(order, product, product.getPrice(), 3);
            order.addOrderItems(List.of(orderItem), BigDecimal.TEN, BigDecimal.TWO);
            Page<Order> pageOrder = new PageImpl<>(List.of(order));

            Mockito.when(userRepository.findById(user.getId()))
                    .thenReturn(Optional.of(user));

            Mockito.when(orderRepository.findByUser(
                    Mockito.eq(user), Mockito.any(Pageable.class)))
                    .thenReturn(pageOrder);

            Page<OrderResponse> response =
                    orderService.findMyOrders(user.getId(), 0, 10);

            assertEquals(1 ,response.getSize());

            Mockito.verify(userRepository).findById(user.getId());
            Mockito.verify(orderRepository).findByUser(
                    Mockito.eq(user), Mockito.any(Pageable.class));
        }
    }

    @Nested
    class FindOrders {

        @Test
        void shouldFindOrdersByStatus(){
            User user = UtilsTest.newUser();
            Product product = UtilsTest.newProduct(5);
            Order order = new Order(user);
            OrderItem orderItem =
                    new OrderItem(order, product, product.getPrice(), 3);
            order.addOrderItems(List.of(orderItem), BigDecimal.TEN, BigDecimal.TWO);
            Page<Order> pageOrder = new PageImpl<>(List.of(order));

            OrderStatus orderStatus = OrderStatus.PENDING_PAYMENT;

            Mockito.when(orderRepository.findByStatus(
                            Mockito.eq(orderStatus), Mockito.any(Pageable.class)))
                    .thenReturn(pageOrder);

            Page<OrderResponse> response =
                    orderService.findOrders(0, 10, orderStatus);

            assertEquals(1 ,response.getSize());

            Mockito.verify(orderRepository).findByStatus(
                    Mockito.eq(orderStatus), Mockito.any(Pageable.class));
        }

        @Test
        void shouldFindOrdersIfStatusIsNull(){
            User user = UtilsTest.newUser();
            Product product = UtilsTest.newProduct(5);
            Order order = new Order(user);
            OrderItem orderItem =
                    new OrderItem(order, product, product.getPrice(), 3);
            order.addOrderItems(List.of(orderItem), BigDecimal.TEN, BigDecimal.TWO);
            Page<Order> pageOrder = new PageImpl<>(List.of(order));

            Mockito.when(orderRepository.findAll(Mockito.any(Pageable.class)))
                    .thenReturn(pageOrder);

            Page<OrderResponse> response =
                    orderService.findOrders(0, 10, null);

            assertEquals(1 ,response.getSize());

            Mockito.verify(orderRepository).findAll(Mockito.any(Pageable.class));
        }
    }

    @Nested
    class ChangePaidOrderToPreparing{

        @Test
        void shouldChangeOrderStatusPaidToPreparing(){
            List<Long> ordersIds = List.of(1L, 2L, 3L, 4L);
            List<Order> orders = genOrders(4);

            Mockito.when(orderRepository.findAllByIdInAndStatus(ordersIds, OrderStatus.PAID))
                    .thenReturn(orders);

            orderService.changePaidOrderToPreparing(ordersIds);

            assertEquals(4, orders.size());

            Mockito.verify(orderRepository).findAllByIdInAndStatus(ordersIds, OrderStatus.PAID);
            Mockito.verify(orderProducer, Mockito.times(4))
                    .sendOrderStatus(Mockito.any());
        }

        @Test
        void shouldThrowIfOrderStatusIsNotPaid(){
            List<Long> ordersIds = List.of(1L, 2L, 3L, 4L);
            List<Order> orders = genOrders(4);

            Mockito.when(orderRepository.findAllByIdInAndStatus(ordersIds, OrderStatus.CANCELED))
                    .thenReturn(orders);

            assertThrows(PotentialStubbingProblem.class,
                    () -> orderService.changePaidOrderToPreparing(ordersIds));
        }

        @Test
        void shouldThrowIfOrdersIsEmpty(){
            List<Long> ordersIds = new ArrayList<>();
            List<Order> orders = new ArrayList<>();

            Mockito.when(orderRepository.findAllByIdInAndStatus(ordersIds, OrderStatus.PAID))
                    .thenReturn(orders);

            assertThrows(ResponseStatusException.class,
                    () -> orderService.changePaidOrderToPreparing(ordersIds));
        }

        private List<Order> genOrders(int quantity){
            List<Order> orders = new ArrayList<>();

            for(int i = 0; i < quantity; i++){
                Order order = new Order(UtilsTest.newUser());
                orders.add(order);
            }

            return orders;
        }
    }

    @Nested
    class CancelOrderIfIsNotPaid{

        @Test
        void shouldCancelOrderIfIsNotPaid(){
            User user = UtilsTest.newUser();
            Order order = new Order(user);

            Mockito.when(orderRepository.findByIdAndUser_IdAndStatus(
                    1L, user.getId(), OrderStatus.PENDING_PAYMENT))
                    .thenReturn(Optional.of(order));

            assertEquals(OrderStatus.PENDING_PAYMENT, order.getStatus());

            orderService.cancelOrderIfIsNotPaid(user.getId(), 1L);

            assertEquals(OrderStatus.CANCELED, order.getStatus());

            Mockito.verify(orderRepository).findByIdAndUser_IdAndStatus(
                    1L, user.getId(), OrderStatus.PENDING_PAYMENT);
            Mockito.verify(orderProducer).sendOrderStatus(Mockito.any());
        }

        @Test
        void shouldThrowIfOrderDoesNotExist(){
            UUID userId = UUID.randomUUID();

            Mockito.when(orderRepository.findByIdAndUser_IdAndStatus(
                            1L, userId, OrderStatus.PENDING_PAYMENT))
                    .thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class,
                    () -> orderService.cancelOrderIfIsNotPaid(userId, 1L));

            Mockito.verify(orderRepository).findByIdAndUser_IdAndStatus(
                    1L, userId, OrderStatus.PENDING_PAYMENT);
        }
    }
}