package com.self.notificationService.model.dto.request;

import com.self.notificationService.enums.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.Map;

@Data
@AllArgsConstructor
public class NotificationRequest {
    private String requestId;
    private Long userId;
    private NotificationType type;
    private Map<String, Object> data;


}
