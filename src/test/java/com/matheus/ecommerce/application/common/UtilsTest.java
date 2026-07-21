package com.matheus.ecommerce.application.common;

import com.matheus.ecommerce.domain.auth.entity.Role;
import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.domain.auth.enums.RoleName;
import com.matheus.ecommerce.domain.catalog.product.entity.Product;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UtilsTest {

    public static User newUser(){
        User user = new User(
                "user_name",
                "email@email.com",
                "raw_password"
        );
        Role role = new Role(RoleName.BASIC);
        user.addRoles(List.of(role));

        return user;
    }

    public static Product newProduct(){
        return new Product(
                "product_name",
                "product_description",
                BigDecimal.TEN,
                5
        );
    }
}
