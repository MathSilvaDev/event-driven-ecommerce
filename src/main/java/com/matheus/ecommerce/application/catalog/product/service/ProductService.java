package com.matheus.ecommerce.application.catalog.product.service;

import com.matheus.ecommerce.application.catalog.product.dto.request.CreateProductRequest;
import com.matheus.ecommerce.application.catalog.product.dto.request.EditProductRequest;
import com.matheus.ecommerce.application.catalog.product.dto.response.ProductResponse;
import com.matheus.ecommerce.application.sales.cart.dto.request.CreateCartItemRequest;
import com.matheus.ecommerce.application.sales.cart.dto.response.CartItemResponse;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.repository.UserRepository;
import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import com.matheus.ecommerce.domain.catalog.product.repository.ProductRepository;
import com.matheus.ecommerce.domain.sales.cart.entity.CartItem;
import com.matheus.ecommerce.domain.sales.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

    public Page<ProductResponse> findAll(int pageNumber, int pageSize){
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,

                Sort.by(
                        Sort.Order.desc("quantity"),
                        Sort.Order.desc("createdAt")
                )
        );

        return productRepository.findAll(pageable)
                .map(this::toResponse);
    }

    public ProductResponse findById(Long id){
        Product product = getProductById(id);
        return toResponse(product);
    }

    public ProductResponse create(CreateProductRequest request){
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.quantity()
        );

        productRepository.save(product);

        return toResponse(product);
    }

    @Transactional
    public void edit(Long id, EditProductRequest request){
        Product product = getProductById(id);

        product.edit(
                request.name(),
                request.description(),
                request.price(),
                request.quantity()
        );
    }

    @Transactional
    public void delete(Long id){
        Product product = getProductById(id);
        //check later if there is any relationships
        productRepository.delete(product);
    }

    @Transactional
    public CartItemResponse addToCart(UUID userId, CreateCartItemRequest request){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));

        Product product = getProductById(request.productId());

        Optional<CartItem> cartItemOpt =
                cartItemRepository.findByCartAndProduct(
                        user.getCart(),
                        product
                );

        CartItem cartItem;
        if(cartItemOpt.isPresent()){
            cartItem = cartItemOpt.get();
            cartItem.addQuantity(request.quantity());
        } else{
            cartItem = new CartItem(
                    user.getCart(),
                    product,
                    request.quantity()
            );
        }

        cartItemRepository.save(cartItem);

        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getQuantity()
        );
    }

    //remove cartItemInCartController

    private Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Product not found"));
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
