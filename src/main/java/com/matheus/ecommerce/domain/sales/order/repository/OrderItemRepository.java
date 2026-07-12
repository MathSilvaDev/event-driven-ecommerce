package com.matheus.ecommerce.domain.sales.order.repository;

import com.matheus.ecommerce.domain.sales.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
}
