package com.axd.notificationService.channel;

import com.axd.notificationService.enums.NotificationChannel;
import com.axd.notificationService.model.ConfigKey;
import com.axd.notificationService.model.dto.request.NotificationRequest;
import com.axd.notificationService.model.dto.response.NotificationSendResult;

public interface NotificationChannelService {
    NotificationSendResult send(NotificationRequest request);

    NotificationChannel getChannelType();

    ConfigKey getConfigKey();
}