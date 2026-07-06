package com.matheus.ecommerce.domain.sales.cart.repository;

import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import com.matheus.ecommerce.domain.sales.cart.entity.Cart;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    Optional<CartItem> findByCartAndProduct_Id(Cart cart, Long productId);
    Optional<CartItem> findByIdAndCart(Long id, Cart cart);

    Page<CartItem> findByCart(Cart cart, Pageable pageable);
}
