package com.axd.notificationService.controller;


import com.axd.notificationService.constants.ControllerConstants;
import com.axd.notificationService.model.dto.request.NotificationRequest;
import com.axd.notificationService.model.dto.response.GenericResponse;
import com.axd.notificationService.model.dto.response.NotificationResponse;
import com.axd.notificationService.model.dto.response.NotificationStatus;
import com.axd.notificationService.model.dto.response.UserNotification;
import com.axd.notificationService.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ControllerConstants.NOTIFICATIONS)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * POST /notifications/send
     * Trigger a new notification.
     */
    @PostMapping(ControllerConstants.SEND)
    public GenericResponse<NotificationResponse> sendNotification(@RequestBody NotificationRequest request) {
        NotificationResponse notificationResponse = notificationService.enqueueNotification(request);
        return GenericResponse.buildSuccess(notificationResponse);
    }

    /**
     * GET /notifications/user/{id}
     * Fetch recent notifications for a user.
     */
    @GetMapping(ControllerConstants.USER_USER_ID)
    public GenericResponse<UserNotification> getUserNotifications(@PathVariable Long userId) {
        UserNotification userNotification = notificationService.getUserNotifications(userId);
        return GenericResponse.buildSuccess(userNotification);
    }

    /**
     * GET /notifications/status/{id}
     * Check the delivery status of a specific notification.
     */
    @GetMapping(ControllerConstants.STATUS_NOTIFICATION_ID)
    public GenericResponse<NotificationStatus> getStatus(@PathVariable Long notificationId) {
        return GenericResponse.buildSuccess(notificationService.getNotificationStatus(notificationId));
    }
}
