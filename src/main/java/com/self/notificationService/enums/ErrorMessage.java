package com.self.notificationService.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorMessage {

    PROVIDER_NOT_AVAILABLE(10001, "Provider not available");

    private final Integer code;
    private final String message;
}
