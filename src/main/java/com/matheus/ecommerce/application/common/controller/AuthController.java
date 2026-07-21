package com.matheus.ecommerce.application.common.controller;

import com.matheus.ecommerce.application.common.dto.request.LoginRequest;
import com.matheus.ecommerce.application.common.dto.request.RefreshRequest;
import com.matheus.ecommerce.application.common.dto.request.RegisterRequest;
import com.matheus.ecommerce.application.common.dto.response.LoginResponse;
import com.matheus.ecommerce.application.common.dto.response.RefreshResponse;
import com.matheus.ecommerce.application.common.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.login(request));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request){
        authService.register(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshResponse> refreshToken(@Valid @RequestBody RefreshRequest refreshToken){
        return ResponseEntity.ok(
                authService.refreshToken(refreshToken));
    }
}
