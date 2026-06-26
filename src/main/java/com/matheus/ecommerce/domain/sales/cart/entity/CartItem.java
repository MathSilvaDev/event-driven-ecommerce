package com.matheus.ecommerce.domain.sales.cart.entity;

import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cart_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @NotNull
    private Integer quantity;

    public CartItem(Cart cart, Product product, Integer quantity){
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }
}
