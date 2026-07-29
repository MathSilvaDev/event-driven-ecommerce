package com.matheus.ecommerce.domain.sales.order.entity;

import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.sales.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "tb_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order",
            orphanRemoval = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    public Order(User user){
        this.user = user;
        this.status = OrderStatus.PENDING_PAYMENT;
    }

    public void addOrderItems(Collection<? extends OrderItem> orderItems){
        this.orderItems.addAll(orderItems);
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }
}
