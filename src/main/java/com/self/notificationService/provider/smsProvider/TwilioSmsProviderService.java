package com.self.notificationService.provider.smsProvider;

import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;
import com.self.notificationService.provider.NotificationProviderService;

public class TwilioSmsProviderService implements NotificationProviderService {
    @Override
    public NotificationSendResult send(NotificationRequest request) {
        return null;
    }

    @Override
    public NotificationProvider getProviderType() {
        return NotificationProvider.TWILIO;
    }

    @Override
    public Boolean isAvailable() {
        return null;
    }
}
