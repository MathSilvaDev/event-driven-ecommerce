package com.matheus.ecommerce.application.sales.order.service;

import com.matheus.ecommerce.application.sales.order.dto.response.OrderItemResponse;
import com.matheus.ecommerce.application.sales.order.dto.response.OrderResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import com.matheus.ecommerce.domain.sales.order.entity.Order;
import com.matheus.ecommerce.domain.sales.order.entity.OrderItem;
import com.matheus.ecommerce.domain.sales.order.enums.OrderStatus;
import com.matheus.ecommerce.domain.sales.order.repository.OrderItemRepository;
import com.matheus.ecommerce.domain.sales.order.repository.OrderRepository;
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

import java.math.BigDecimal;
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

        User user = findUserById(userId);

        List<CartItem> cartItems = user.getCart().getCartItems()
                .stream()
                .filter(CartItem::isSelected)
                .toList();

        cartItems.forEach((i) -> {
            if(i.getProduct().isUnavailable(i.getQuantity())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "One or more products have an unavailable quantity");
            }else{
                i.getProduct().removeQuantity(i.getQuantity());
            }
        });

        List<OrderItem> orderItems = cartItems
                .stream()
                .map(i -> new OrderItem(
                        i.getProduct(),
                        i.getProduct().getPrice(),
                        i.getQuantity()))
                .toList();

        orderItemRepository.saveAll(orderItems);

        Order order = new Order(user, orderItems);
        user.getOrders().add(order);

        orderRepository.save(order);

        user.getCart().removeItems(cartItems);
        cartItemRepository.deleteAll(cartItems);

        return toResponse(order);
    }

    public Page<OrderResponse> findMyOrders(UUID userId, int pageNumber, int pageSize){
        User user = findUserById(userId);

        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by("createdAt").descending()
        );

        return orderRepository.findByUser(user, pageable)
                .map(this::toResponse);

    }

    public Page<OrderResponse> findOrders(int pageNumber, int pageSize,
                                          OrderStatus orderStatus){

        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by("createdAt")
        );

        if(orderStatus == null){
            return orderRepository.findAll(pageable)
                    .map(this::toResponse);
        }

        return orderRepository.findByStatus(orderStatus, pageable)
                .map(this::toResponse);

    }

    private OrderResponse toResponse(Order order){
        List<OrderItem> orderItems = order.getOrderItems();

        BigDecimal price = orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int quantity = orderItems.stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                price.multiply(BigDecimal.valueOf(quantity)),

                orderItems
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
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getPrice().multiply(
                        BigDecimal.valueOf(orderItem.getQuantity()))
        );
    }

    private User findUserById(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
