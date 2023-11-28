package com.example.library.exceptions;

public class DuplicateWishlistNameException extends RuntimeException{
    public DuplicateWishlistNameException(String message) {
        super(message);
    }
}
