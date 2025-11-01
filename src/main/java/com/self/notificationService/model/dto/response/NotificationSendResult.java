package com.self.notificationService.model.dto.response;

import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.enums.NotificationRequestStatus;
import lombok.Data;

@Data
public class NotificationSendResult {
    private NotificationRequestStatus status;
    private NotificationProvider provider;
    private String externalId;
    private String errorMessage;
}
