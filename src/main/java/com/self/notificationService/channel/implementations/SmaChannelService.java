package com.self.notificationService.channel.implementations;

import com.self.notificationService.channel.NotificationChannelService;
import com.self.notificationService.enums.NotificationChannel;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;

public class SmaChannelService implements NotificationChannelService {
    @Override
    public NotificationSendResult send(NotificationRequest request) {
        return null;
    }

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.SMS;
    }
}
