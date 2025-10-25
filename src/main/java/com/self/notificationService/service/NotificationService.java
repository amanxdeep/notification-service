package com.self.notificationService.service;

import com.self.notificationService.kafka.KafkaProducer;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationResponse;
import com.self.notificationService.model.dto.response.NotificationStatus;
import com.self.notificationService.model.dto.response.UserNotification;
import com.self.notificationService.model.entity.NotificationLog;
import com.self.notificationService.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaProducer kafkaProducer;
    private final NotificationLogRepository notificationLogRepository;

    /**
     * Step 1: Enqueue notification request to Kafka topic.
     */

    public NotificationResponse enqueueNotification(NotificationRequest request) {
        return null;
    }

   // public void enqueueNotification(NotificationRequest request) {
     //   kafkaProducer.sendEvent("notification_requests", request);
    //}

    /**
     * Step 2: Retrieve user’s recent notifications from DB.
     */
    public UserNotification getUserNotifications(Long userId) {
        List<NotificationLog> notificationLogs =  notificationLogRepository.findByUserId(userId);
        return new UserNotification(notificationLogs);
    }

    /**
     * Step 3: Retrieve specific notification status.
     */
    public NotificationStatus getNotificationStatus(Long id) {
        Optional<NotificationLog> notificationLogOptional = notificationLogRepository.findById(id);
        String status;
        if(notificationLogOptional.isPresent()) {
            status = notificationLogOptional.get().getStatus();
        } else {
            status = "NOT_FOUND";
        }
        return new NotificationStatus(status);
    }


}
