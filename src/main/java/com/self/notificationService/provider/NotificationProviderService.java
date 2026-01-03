package com.self.notificationService.provider;

import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;

public interface NotificationProviderService {
    NotificationSendResult send(NotificationRequest request);
    NotificationProvider getProviderType();
    Boolean isAvailable();
}
