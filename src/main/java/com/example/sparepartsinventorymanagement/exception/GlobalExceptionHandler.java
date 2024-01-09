package com.example.sparepartsinventorymanagement.exception;

import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(QuantityExceedsInventoryException.class)
    public ResponseEntity<ResponseObject> handleQuantityExceedsInventoryException(QuantityExceedsInventoryException ex) {
        ResponseObject responseObject = ResponseObject.builder()
                .status(HttpStatus.BAD_REQUEST.toString())
                .message(ex.getMessage())
                .data(null) // hoặc bạn có thể cung cấp thêm dữ liệu nếu cần
                .build();

        return new ResponseEntity<>(responseObject, HttpStatus.BAD_REQUEST);
    }
}
