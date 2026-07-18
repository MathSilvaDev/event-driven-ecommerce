package com.matheus.ecommerce.application.catalog.product.service;

import com.matheus.ecommerce.application.catalog.product.dto.request.CreateProductRequest;
import com.matheus.ecommerce.application.catalog.product.dto.request.EditProductRequest;
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
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

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

    @Nested
    class FindById{

        @Test
        void shouldFindByIdSuccessfully(){
            Product product = newProduct();

            Mockito.when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            ProductResponse response = productService.findById(product.getId());

            assertEquals(product.getId(), response.id());
            assertEquals(product.getName(), response.name());

            Mockito.verify(productRepository).findById(product.getId());
        }

        @Test
        void shouldThrowIfProductWasNotFoundById(){
            Mockito.when(productRepository.findById(1L))
                    .thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class,
                    () -> productService.findById(1L));

            Mockito.verify(productRepository).findById(1L);
        }

    }

    @Nested
    class Create{

        @Test
        void shouldCreateSuccessfully(){
            Product product = newProduct();
            CreateProductRequest request = new CreateProductRequest(
                    product.getName(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity()
            );

            Mockito.when(productRepository.save(any(Product.class)))
                    .thenReturn(product);

            ProductResponse response = productService.create(request);

            assertEquals(request.quantity(), response.quantity());
            assertEquals(request.price(), response.price());

            Mockito.verify(productRepository).save(any(Product.class));
        }
    }

    @Nested
    class Edit{

        @Test
        void shouldEditSuccessfully(){
            Product product = newProduct();
            EditProductRequest request = new EditProductRequest(
                    "new_name",
                    null,
                    BigDecimal.valueOf(25),
                    30
            );

            Mockito.when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            productService.edit(product.getId(), request);

            assertEquals(request.name(), product.getName());
            assertEquals("product_description", product.getDescription());
            assertNotNull(product.getDescription());
            assertEquals(request.name(), product.getName());
            assertEquals(request.quantity(), product.getQuantity());

            Mockito.verify(productRepository).findById(product.getId());
        }

        @Test
        void shouldThrowProductWasNotFound(){
            Product product = newProduct();
            EditProductRequest request = new EditProductRequest(
                    "new_name",
                    null,
                    BigDecimal.valueOf(25),
                    30
            );

            Mockito.when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.empty());

            assertThrows(ResponseStatusException.class,
                    () -> productService.edit(product.getId(), request));

            assertNotEquals(request.name(), product.getName());
            assertNotNull(product.getDescription());
            assertNull(request.description());
            assertNotEquals(request.name(), product.getName());
            assertNotEquals(request.quantity(), product.getQuantity());

            Mockito.verify(productRepository).findById(product.getId());
        }
    }

    @Nested
    class Delete{

        @Test
        void shouldDeleteSuccessfully(){
            Product product = newProduct();

            Mockito.when(productRepository.findById(product.getId()))
                    .thenReturn(Optional.of(product));

            productService.delete(product.getId());

            Mockito.verify(productRepository).findById(product.getId());
            Mockito.verify(productRepository).delete(product);
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

}