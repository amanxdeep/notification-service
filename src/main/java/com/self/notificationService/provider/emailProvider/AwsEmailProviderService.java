package com.self.notificationService.provider.emailProvider;
import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;
import com.self.notificationService.provider.NotificationProviderService;

public class AwsEmailProviderService implements NotificationProviderService {
    @Override
    public NotificationSendResult send(NotificationRequest request) {
        return null;
    }

    @Override
    public NotificationProvider getProviderType() {
        return NotificationProvider.AWS;
    }

    @Override
    public Boolean isAvailable() {
        return null;
    }
}
