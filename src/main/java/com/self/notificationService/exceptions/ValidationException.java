package com.self.notificationService.exceptions;

import lombok.Data;

@Data
public class ValidationException extends RuntimeException implements AbstractException{

    Integer statusCode;
    String message;

    public  ValidationException(String message ,  Integer statusCode){
        super(message);
        this.statusCode = statusCode;
    }

    public  ValidationException(String message){
        super(message);
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getErrorMessage() {
        return getMessage();
    }
}
