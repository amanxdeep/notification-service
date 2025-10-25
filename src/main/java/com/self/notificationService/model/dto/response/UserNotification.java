package com.self.notificationService.model.dto.response;

import com.self.notificationService.model.entity.NotificationLog;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class UserNotification {
    private List<NotificationLog> notifications;
}
