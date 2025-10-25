package com.self.notificationService.controller;


import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.GenericResponse;
import com.self.notificationService.model.dto.response.NotificationResponse;
import com.self.notificationService.model.dto.response.NotificationStatus;
import com.self.notificationService.model.dto.response.UserNotification;
import com.self.notificationService.model.entity.NotificationLog;
import com.self.notificationService.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * POST /notifications/send
     * Trigger a new notification.
     */
    @PostMapping("/send")
    public GenericResponse<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        NotificationResponse notificationResponse = notificationService.enqueueNotification(request);
        return GenericResponse.buildSuccess(notificationResponse);
    }

    /**
     * GET /notifications/user/{id}
     * Fetch recent notifications for a user.
     */
    @GetMapping("/user/{userId}")
    public GenericResponse<UserNotification> getUserNotifications(@PathVariable Long userId) {
        UserNotification userNotification = notificationService.getUserNotifications(userId);
        return GenericResponse.buildSuccess(userNotification);
    }

    /**
     * GET /notifications/status/{id}
     * Check the delivery status of a specific notification.
     */
    @GetMapping("/status/{notificationId}")
    public GenericResponse<NotificationStatus> getStatus(@PathVariable Long notificationId) {
        return GenericResponse.buildSuccess(notificationService.getNotificationStatus(notificationId));
    }
}
