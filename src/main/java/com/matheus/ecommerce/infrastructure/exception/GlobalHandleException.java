package com.matheus.ecommerce.infrastructure.exception;

import com.matheus.ecommerce.infrastructure.exception.dto.ApiError;
import com.matheus.ecommerce.infrastructure.exception.jwt.InvalidRefreshTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalHandleException{

    private ResponseEntity<ApiError> toResponse(Object message, HttpStatus httpStatus){
        return ResponseEntity
                .status(httpStatus)
                .body(new ApiError(message, httpStatus));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiError> invalidRefreshTokenException(InvalidRefreshTokenException e){
        return toResponse(e.getMessage(), e.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> exception(Exception e){
        log.error(e.getMessage());
        return toResponse("UNKNOWN ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
