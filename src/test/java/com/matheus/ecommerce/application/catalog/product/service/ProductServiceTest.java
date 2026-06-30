package com.matheus.ecommerce.application.catalog.product.service;

import com.matheus.ecommerce.application.catalog.product.dto.response.ProductResponse;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import com.matheus.ecommerce.domain.catalog.product.repository.ProductRepository;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private ProductService productService;

    @Nested
    class FindAll{

        @Test
        void shouldFindAllSuccessfully(){

            Product product = newProduct();
            List<Product> productList = List.of(product);
            Page<Product> page = new PageImpl<>(productList);

            Mockito.when(productRepository.findAll(any(Pageable.class)))
                    .thenReturn(page);

            Page<ProductResponse> response = productService.findAll(0, 10);

            assertEquals(productList.size(), page.getContent().size());
            assertEquals(1, response.getContent().size());

            Mockito.verify(productRepository).findAll(any(Pageable.class));
        }
    }

    private Product newProduct(){
        return new Product(
                "product_name",
                "product_description",
                BigDecimal.TEN,
                5
        );
    }

    private ProductResponse toResponse(Product product){
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity()
        );
    }
}