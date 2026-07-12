package com.matheus.ecommerce.infrastructure.exception.jwt;

public class InvalidRefreshTokenException extends RuntimeException{

    public InvalidRefreshTokenException(){
        super("Refresh token expired or invalid");
    }

    public InvalidRefreshTokenException(String message){
        super(message);
    }

}
