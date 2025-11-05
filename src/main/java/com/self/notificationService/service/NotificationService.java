package com.self.notificationService.service;

import com.self.notificationService.exceptions.ResourceNotFoundException;
import com.self.notificationService.exceptions.ValidationException;
import com.self.notificationService.kafka.KafkaProducer;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationResponse;
import com.self.notificationService.model.dto.response.NotificationStatus;
import com.self.notificationService.model.dto.response.UserNotification;
import com.self.notificationService.model.entity.NotificationLog;
import com.self.notificationService.repository.NotificationLogRepository;
import com.self.notificationService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final KafkaProducer kafkaProducer;
    private final NotificationLogRepository notificationLogRepository;
    private final UserRepository userRepository;

    /**
     *  Enqueue notification request to Kafka topic.
     *  it checks if the notification request is already processed or not
     */
    public NotificationResponse enqueueNotification(NotificationRequest request) {

        NotificationResponse existingLog = isAlreadyProcessed(request);
        if (existingLog != null) return existingLog;
        return null;
    }

    private NotificationResponse isAlreadyProcessed(NotificationRequest request) {
        Optional<NotificationLog> notificationLogOptional = notificationLogRepository.findByRequestId(request.getRequestId());
        if(notificationLogOptional.isPresent()){
            NotificationLog existingLog = notificationLogOptional.get();
            return new NotificationResponse(existingLog.getNotificationId(), existingLog.getState());
        }
        return null;
    }

    /**
      Retrieve user’s recent notifications from DB.
     */
    public UserNotification getUserNotifications(Long userId) {
        List<NotificationLog> notificationLogs =  notificationLogRepository.findByUserId(userId);
        return new UserNotification(notificationLogs);
    }

    /**
      Retrieve specific notification status.
     */
    public NotificationStatus getNotificationStatus(Long id) {
        Optional<NotificationLog> notificationLogOptional = notificationLogRepository.findById(id);
        String status;
        if(notificationLogOptional.isPresent()) {
            status = notificationLogOptional.get().getState();
        } else {
            status = "NOT_FOUND";
        }
        return new NotificationStatus(status);
    }

}
