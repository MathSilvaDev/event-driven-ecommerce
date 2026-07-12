package com.matheus.ecommerce.infrastructure.security.jwt.service;

import com.matheus.ecommerce.domain.auth.entity.User;
import com.matheus.ecommerce.infrastructure.exception.jwt.InvalidRefreshTokenException;
import com.matheus.ecommerce.application.auth.dto.response.TokenDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    private static final long ACCESS_TOKEN_EXPIRES_IN = 300L; //5 min
    private static final long REFRESH_TOKEN_EXPIRES_IN = 60L * 60L * 24L * 7L; //1 week

    public TokenDTO generateAccessToken(User user){
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(ACCESS_TOKEN_EXPIRES_IN);

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("BACKEND_Matheus")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("roles",
                        user.getRoles()
                                .stream()
                                .map((r) -> r.getName().name())
                                .toList()
                )
                .claim("token_type", "access")
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();

        return new TokenDTO(token, expiresAt);
    }

    public TokenDTO generateRefreshToken(User user){
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(REFRESH_TOKEN_EXPIRES_IN);

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("BACKEND_Matheus")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("token_type", "refresh")
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(claimsSet)).getTokenValue();

        return new TokenDTO(token, expiresAt);
    }

    public UUID extractUserId(String refreshToken){
        Jwt jwt = decodeTokenIfIsValid(refreshToken);
        return UUID.fromString(jwt.getSubject());
    }

    public TokenDTO refreshToken(String refreshToken, User user){
        Jwt jwt = decodeTokenIfIsValid(refreshToken);

        boolean isRefreshType = "refresh".equals(jwt.getClaimAsString("token_type"));

        if(!isRefreshType){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token type");
        }

        if(!user.getRefreshToken().getToken().equals(refreshToken)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid refresh token");
        }

        return generateAccessToken(user);
    }

    private Jwt decodeTokenIfIsValid(String token){
        try {
            return jwtDecoder.decode(token);
        } catch (JwtException e) {
            throw new InvalidRefreshTokenException();
        }
    }
}
