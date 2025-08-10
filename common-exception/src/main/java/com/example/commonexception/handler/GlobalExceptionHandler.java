package com.example.commonexception.handler;

import com.example.commonexception.exceptions.BadRequestException;
import com.example.commonexception.exceptions.ConflictException;
import com.example.commonexception.exceptions.ResourceNotFoundException;
import com.example.commonexception.exceptions.UnauthorizedException;
import com.example.commonexception.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

public class GlobalExceptionHandler {
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
    }

    public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        // Burada loglama yapabilirsin
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, WebRequest request) {
        ErrorResponse error = ErrorResponse.builder()
                .message(message)
                .httpStatus(status.value())
                .responseTime(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, status);
    }
}