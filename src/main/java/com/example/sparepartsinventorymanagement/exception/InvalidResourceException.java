package com.example.sparepartsinventorymanagement.exception;

public class InvalidResourceException extends RuntimeException{
    public InvalidResourceException(String message){
        super(message);
    }
}
