package com.self.notificationService.channel.implementations;

import com.self.notificationService.channel.NotificationChannelService;
import com.self.notificationService.enums.NotificationChannel;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;

import org.springframework.stereotype.Service;
@Service
public class WhatsAppChannelService implements NotificationChannelService {

    @Override
    public NotificationSendResult send(NotificationRequest request) {
        return null;
    }

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.WHATSAPP;
    }

    private String getMessageBody(NotificationRequest notificationRequest) {
        return null;
    }
}
