package com.matheus.ecommerce.common.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthUtils {

    public static UUID getUserIdByJwt(Jwt jwt){
        return UUID.fromString(jwt.getSubject());
    }
}
