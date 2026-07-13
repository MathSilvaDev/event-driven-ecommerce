package com.matheus.ecommerce.application.sales.order.service;

import com.matheus.ecommerce.application.sales.order.dto.response.OrderItemResponse;
import com.matheus.ecommerce.application.sales.order.dto.response.OrderResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import com.matheus.ecommerce.domain.sales.order.entity.Order;
import com.matheus.ecommerce.domain.sales.order.entity.OrderItem;
import com.matheus.ecommerce.domain.sales.order.repository.OrderItemRepository;
import com.matheus.ecommerce.domain.sales.order.repository.OrderRepository;
import com.matheus.ecommerce.infrastructure.exception.auth.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public OrderResponse createOrder(UUID userId){

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<CartItem> cartItems = user.getCart().getCartItems()
                .stream()
                .filter(CartItem::isSelected)
                .toList();

        List<OrderItem> orderItems = cartItems
                .stream()
                .map(i -> new OrderItem(i.getProduct(), i.getQuantity()))
                .toList();

        orderItemRepository.saveAll(orderItems);

        Order order = new Order(user, orderItems);
        orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems);

        return toResponse(order);
    }

    private OrderResponse toResponse(Order order){
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getOrderItems()
                        .stream()
                        .map(this::toItemResponse)
                        .toList(),
                order.getCreatedAt()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem orderItem){
        return new OrderItemResponse(
                orderItem.getId(),
                orderItem.getProduct().getName(),
                orderItem.getQuantity()
        );
    }
}
