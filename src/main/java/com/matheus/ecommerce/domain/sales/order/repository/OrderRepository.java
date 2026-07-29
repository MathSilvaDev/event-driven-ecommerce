package com.matheus.ecommerce.domain.sales.order.repository;

import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.sales.order.entity.Order;
import com.matheus.ecommerce.domain.sales.order.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByIdAndUser_Id(Long id, UUID userId);

    List<Order> findAllByIdAndStatus(List<Long> id, OrderStatus orderStatus);
    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, Instant instant);

    Page<Order> findByUser(User user, Pageable pageable);
    Page<Order> findByStatus(OrderStatus orderStatus, Pageable pageable);

}
