package com.example.sparepartsinventorymanagement.exception;

import com.example.sparepartsinventorymanagement.dto.response.ErrorResponse;
import com.example.sparepartsinventorymanagement.utils.ResponseObject;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.management.relation.RoleNotFoundException;
import java.io.FileNotFoundException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@CrossOrigin
@RestControllerAdvice
@Log4j2
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse accessDenied(AccessDeniedException e){
        return new ErrorResponse(new Date(), HttpStatus.FORBIDDEN.toString(), "Access is denied");
    }



    @ExceptionHandler(value = RoleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRoleNotFoundException(RoleNotFoundException ex, WebRequest request) {
        logger.error(ex.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NOT_FOUND.toString(), ex.getMessage());
    }

    @ExceptionHandler({FileNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerFileNotFoundException(Exception e, WebRequest request) {
        logger.error(e.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
    }
    @ExceptionHandler({RuntimeException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(Exception e, WebRequest request) {
        logger.error(e.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), e.getMessage());
    }

//    @ExceptionHandler(value = com.java8.tms.user.custom_exception.RoleAuthorizationAccessDeniedException.class)
//    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
//    public ErrorResponse handleRoleAuthorizationAccessDeniedException(com.java8.tms.user.custom_exception.RoleAuthorizationAccessDeniedException ex, WebRequest request) {
//        logger.error(ex.getMessage());
//        return new ErrorResponse(new Date(), HttpStatus.NOT_ACCEPTABLE.toString(), ex.getMessage());
//    }

    // Xử lý tất cả các exception chưa được khai báo
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerException(Exception e, WebRequest request) {
        e.printStackTrace();
        return new ErrorResponse(new Date(), HttpStatus.BAD_REQUEST.toString(), e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerException(Throwable exception, WebRequest request) {
        log.error(exception.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.BAD_REQUEST.toString(), exception.getMessage());
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ErrorResponse resourceNotFoundException(ResourceNotFoundException exception, WebRequest request) {
        log.error(exception.getMessage());
        return new ErrorResponse(new Date(), HttpStatus.NO_CONTENT.toString(), exception.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        final List<String> errors = new ArrayList<>();
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        logger.error(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseObject(HttpStatus.BAD_REQUEST.toString(), "Invalid request", errors));
    }
}