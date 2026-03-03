package com.axd.notificationService.exceptions;

public interface AbstractException {
    int getStatusCode();
    String getErrorMessage();

}
