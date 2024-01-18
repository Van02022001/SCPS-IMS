package com.example.sparepartsinventorymanagement.exception;

public class InvalidInventoryDataException extends RuntimeException{
    public InvalidInventoryDataException(String message){
        super(message);
    }
}
