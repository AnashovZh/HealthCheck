package com.example.healthcheckb10.exceptions;

public class BadCredentialException extends RuntimeException{
    public BadCredentialException(String message){
        super(message);
    }
}