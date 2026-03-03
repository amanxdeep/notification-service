package com.axd.notificationService.provider;

import com.axd.notificationService.enums.NotificationProvider;
import com.axd.notificationService.model.dto.request.NotificationRequest;
import com.axd.notificationService.model.dto.response.NotificationSendResult;

public interface NotificationProviderService {
    NotificationSendResult send(NotificationRequest request);
    NotificationProvider getProviderType();
    Boolean isAvailable();
}
