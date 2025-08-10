package com.example.adminms.handler;

import com.example.commonexception.exceptions.BadRequestException;
import com.example.commonexception.exceptions.ResourceNotFoundException;
import com.example.commonexception.handler.GlobalExceptionHandler;
import com.example.commonexception.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;


@RestControllerAdvice
public class GlobalHandler extends GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return super.handleResourceNotFound(ex, request);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, WebRequest request) {
        return super.handleBadRequest(ex, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex, WebRequest request) {
        return super.handleGenericException(ex, request);
    }
}
