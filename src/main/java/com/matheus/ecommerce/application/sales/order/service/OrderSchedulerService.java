package com.matheus.ecommerce.application.sales.order.service;

import com.matheus.ecommerce.domain.sales.order.entity.Order;
import com.matheus.ecommerce.domain.sales.order.enums.OrderStatus;
import com.matheus.ecommerce.domain.sales.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderSchedulerService {

    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void expiredPendingOrders(){
        Instant limit = Instant.now().minus(24, ChronoUnit.HOURS);

        List<Order> orders = orderRepository.findByStatusAndCreatedAtBefore(
                OrderStatus.PENDING_PAYMENT,
                limit
        );

        orders.forEach(order -> {
                order.setStatus(OrderStatus.EXPIRED);
                log.info("Order {} expired",order.getId());
        });
    }
}
