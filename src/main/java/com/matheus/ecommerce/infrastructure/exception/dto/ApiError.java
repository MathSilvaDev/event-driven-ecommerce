package com.matheus.ecommerce.infrastructure.exception.dto;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ApiError(
    Object message,
    int status,
    Instant timestamp
) {
    public ApiError(Object message, HttpStatus httpStatus){
        this(
                message,
                httpStatus.value(),
                Instant.now()
        );
    }
}
