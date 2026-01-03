package com.self.notificationService.exceptions.handler;

import com.self.notificationService.exceptions.ResourceNotFoundException;
import com.self.notificationService.exceptions.ValidationException;
import com.self.notificationService.model.dto.response.GenericError;
import com.self.notificationService.model.dto.response.GenericResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionsHandler {

    /**
     * Handles ValidationException - Custom validation errors
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<GenericResponse<Object>> handleValidationException(ValidationException ex) {
        log.warn("Validation exception: {}", ex.getMessage());
        
        HttpStatus status = ex.getStatusCode() != 0
                ? HttpStatus.valueOf(ex.getStatusCode()) 
                : HttpStatus.BAD_REQUEST;
        
        return buildErrorResponse(status, ex.getMessage());
    }

    /**
     * Handles ResourceNotFoundException - Resource not found errors
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<GenericResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        
        HttpStatus status = ex.getStatusCode() != 0
                ? HttpStatus.valueOf(ex.getStatusCode()) 
                : HttpStatus.NOT_FOUND;
        
        return buildErrorResponse(status, ex.getMessage());
    }

    /**
     * Catch-all handler for any unhandled exceptions.
     * Logs the full stack trace for debugging and returns a generic error response.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<Object>> handleUnhandledException(Exception ex) {
        log.error("Unhandled exception occurred: ", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred. Please try again later.");
    }

    /**
     * Builds a consistent error response with GenericResponse structure
     */
    private ResponseEntity<GenericResponse<Object>> buildErrorResponse(HttpStatus status, String message) {
        GenericError error = new GenericError();
        error.setCode(status.value());
        error.setMessage(message);
        
        return ResponseEntity
                .status(status)
                .body(GenericResponse.buildFailure(error));
    }
}
