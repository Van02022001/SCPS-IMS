package com.example.sparepartsinventorymanagement.exception;

public class QuantityExceedsInventoryException extends RuntimeException {
    public QuantityExceedsInventoryException(String message) {
        super(message);
    }
}
