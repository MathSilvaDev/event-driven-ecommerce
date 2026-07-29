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
import com.matheus.ecommerce.infrastructure.kafka.order.producer.OrderProducer;
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
    private final OrderProducer orderProducer;

    @Transactional
    public OrderResponse createOrder(UUID userId){

        User user = getUserById(userId);

        List<CartItem> cartItems = user.getCart().getCartItems()
                .stream()
                .filter(CartItem::isSelected)
                .toList();

        if(cartItems.isEmpty()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cart is empty");
        }

        cartItems.forEach((i) -> {
            if(!i.getProduct().isAvailable(i.getQuantity())){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "One or more products have an unavailable quantity");
            }else{
                i.getProduct().removeQuantity(i.getQuantity());
            }
        });
        Order order = new Order(user);

        List<OrderItem> orderItems = cartItems
                .stream()
                .map(i -> new OrderItem(
                        order,
                        i.getProduct(),
                        i.getProduct().getPrice(),
                        i.getQuantity()
                        ))
                .toList();

        order.addOrderItems(orderItems);
        orderRepository.save(order);
        orderItemRepository.saveAll(orderItems);

        user.getOrders().add(order);
        user.getCart().removeItems(cartItems);
        cartItemRepository.deleteAll(cartItems);

        orderProducer.sendOrderCreated(order.getId().toString());

        return toResponse(order);
    }

    public Page<OrderResponse> findMyOrders(UUID userId, int pageNumber, int pageSize){
        User user = getUserById(userId);

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
    @Transactional
    public void changePaidOrderToPreparing(List<Long> orderIds){
        orderRepository.findAllByIdAndStatus(orderIds, OrderStatus.PAID)
                .forEach(order -> order.setStatus(OrderStatus.PREPARING));
    }

    @Transactional
    public void simulatePayment(UUID userId, Long id){
        Order order = orderRepository.findByIdAndUser_Id(id, userId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Order not found"));

        order.setStatus(OrderStatus.PAID);
        orderProducer.sendOrderPaid(order.getId().toString());
    }

    private OrderResponse toResponse(Order order){
        List<OrderItem> orderItems = order.getOrderItems();

        BigDecimal totalPrice = orderItems.stream()
                .map(item ->
                        item.getPrice().multiply(BigDecimal.valueOf(
                                item.getQuantity()
                        )))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                totalPrice,

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

    private User getUserById(UUID userId){
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
