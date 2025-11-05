package com.self.notificationService.exceptions.handler;

import java.util.function.Supplier;
import com.self.notificationService.exceptions.AbstractException;
import com.self.notificationService.exceptions.ResourceNotFoundException;
import com.self.notificationService.exceptions.ValidationException;
import com.self.notificationService.util.CommonUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionsHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<String> handleValidationException(ValidationException validationException){

        return genericExceptionHandler(validationException ,
                ()-> ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(validationException.getMessage())) ;

        /* if(CommonUtils.isNotEmpty(validationException.getStatusCode())){
            return ResponseEntity.status(validationException.getStatusCode()).body(validationException.getMessage());
        }
        return ResponseEntity.badRequest().body(validationException.getMessage());*/
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException){

        return genericExceptionHandler(resourceNotFoundException ,
                ()-> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(resourceNotFoundException.getMessage())) ;

       /* if(CommonUtils.isNotEmpty(resourceNotFoundException.getStatusCode())){
            return ResponseEntity.status(resourceNotFoundException.getStatusCode()).body(resourceNotFoundException.getMessage());
        }
        return ResponseEntity.badRequest().body(resourceNotFoundException.getMessage());*/
    }

    public ResponseEntity<String> genericExceptionHandler(final AbstractException abstractException ,
                                        final Supplier<ResponseEntity<String>> runner){
        if (CommonUtils.isNotEmpty(abstractException.getStatusCode())){
            return ResponseEntity.status(abstractException.getStatusCode()).body(abstractException.getErrorMessage());
        }
        return runner.get();
    }

}
