package com.example.sparepartsinventorymanagement.exception;

public class InvalidStatusException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InvalidStatusException(String resource, String message) {
        super(String.format("Failed for [%s]: %s", resource, message));
    }

}
