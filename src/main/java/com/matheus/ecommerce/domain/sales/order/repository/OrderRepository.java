package com.matheus.ecommerce.domain.sales.order.repository;

import com.matheus.ecommerce.domain.sales.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
