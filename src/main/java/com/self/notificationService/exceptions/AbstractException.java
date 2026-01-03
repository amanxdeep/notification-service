package com.self.notificationService.exceptions;

public interface AbstractException {
    int getStatusCode();
    String getErrorMessage();

}
