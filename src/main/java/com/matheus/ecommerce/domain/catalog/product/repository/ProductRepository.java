package com.matheus.ecommerce.domain.catalog.product.repository;

import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
