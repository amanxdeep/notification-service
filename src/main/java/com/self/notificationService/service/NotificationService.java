package com.self.notificationService.service;

import com.self.notificationService.channel.NotificationChannelService;
import com.self.notificationService.enums.NotificationChannel;
import com.self.notificationService.enums.NotificationRequestStatus;
import com.self.notificationService.exceptions.ResourceNotFoundException;
import com.self.notificationService.exceptions.ValidationException;
import com.self.notificationService.factory.ChannelFactory;
import com.self.notificationService.kafka.KafkaProducer;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationResponse;
import com.self.notificationService.model.dto.response.NotificationSendResult;
import com.self.notificationService.model.dto.response.NotificationStatus;
import com.self.notificationService.model.dto.response.UserNotification;
import com.self.notificationService.model.entity.NotificationLog;

import com.self.notificationService.repository.NotificationLogRepository;
import com.self.notificationService.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final UserRepository userRepository;
    private final ChannelFactory channelFactory;
    private final NotificationLogService notificationLogService;

    /**
     *  Enqueue notification request to Kafka topic.
     *  it checks if the notification request is already processed or not
     */
    public NotificationResponse enqueueNotification(NotificationRequest request) {

        NotificationResponse existingLog = isAlreadyProcessed(request);
        if (!ObjectUtils.isEmpty(existingLog)) {
            return existingLog;
        }
        return sendNotificationDirectly(request);
    }

    public NotificationResponse sendNotificationDirectly(NotificationRequest request) {
        try {
            NotificationChannel channel = NotificationChannel.EMAIL;

            NotificationChannelService channelService = channelFactory.getChannel(channel);
            NotificationSendResult result = channelService.send(request);

            // Log the notification attempt
            String notificationId = UUID.randomUUID().toString();
            if(loggableEvent(result))
                notificationLogService.logNotification(request, result, notificationId, channel);

            return new NotificationResponse(notificationId, result.getStatus().toString());

        } catch (Exception ex) {
            log.error("Exception occurred while sending notifictication ", ex.getMessage());
            return new NotificationResponse(null, NotificationRequestStatus.FAILURE.toString());
        }
    }

    private NotificationResponse isAlreadyProcessed(NotificationRequest request) {
        Optional<NotificationLog> notificationLogOptional = notificationLogRepository.findByRequestId(request.getRequestId());
        if(notificationLogOptional.isPresent()) {
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

    private boolean loggableEvent(NotificationSendResult result){
        return !NotificationRequestStatus.FAILURE.equals(result.getStatus());
    }

}
