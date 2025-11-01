package com.self.notificationService.channel;

import com.self.notificationService.enums.NotificationChannel;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;

public interface NotificationChannelService {
    NotificationSendResult send(NotificationRequest request);

    NotificationChannel getChannelType();
}