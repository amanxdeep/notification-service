package com.self.notificationService.model;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ConfigKey {

    EMAIL_PROVIDER_CONFIG("notification.providers", "email"),
    SMS_PROVIDER_CONFIG("notification.providers", "sms"),
    WHATSAPP_PROVIDER_CONFIG("notification.providers", "whatsapp");
    
    private final String group;
    private final String key;
}