package com.example.commonexception.exceptions;


public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}