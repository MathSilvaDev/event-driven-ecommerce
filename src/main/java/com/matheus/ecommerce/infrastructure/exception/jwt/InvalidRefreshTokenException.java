package com.matheus.ecommerce.infrastructure.exception.jwt;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidRefreshTokenException extends RuntimeException{

    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;

    public InvalidRefreshTokenException(){
        super("Refresh token expired or invalid");
    }

    public InvalidRefreshTokenException(Throwable cause){
        super("Refresh token expired or invalid", cause);
    }

    public InvalidRefreshTokenException(String message, Throwable cause){
        super(message, cause);
    }

}
