package com.example.library.service.Impl;

public class InsufficientQuantityException extends RuntimeException {


    public InsufficientQuantityException(String message){
        super(message);
    }
}
