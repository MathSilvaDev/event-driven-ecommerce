package com.matheus.ecommerce.domain.catalog.product.entity;

import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItem = new ArrayList<>();

    public Product(String name,String description, BigDecimal price, Integer quantity){
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public void edit(String name, String description, BigDecimal price, Integer quantity){
        this.name = name == null ? this.name : name;
        this.description = description == null ? this.description : description;
        this.price = price == null ? this.price : price;
        this.quantity = quantity == null ? this.quantity : quantity;
    }

    public boolean isAvailable(){
        return quantity > 0;
    }

}
