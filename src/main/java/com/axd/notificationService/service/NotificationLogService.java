package com.axd.notificationService.service;

import com.axd.notificationService.enums.NotificationChannel;
import com.axd.notificationService.exceptions.ResourceNotFoundException;
import com.axd.notificationService.exceptions.ValidationException;
import com.axd.notificationService.model.dto.request.NotificationRequest;
import com.axd.notificationService.model.dto.response.NotificationSendResult;
import com.axd.notificationService.model.entity.NotificationLog;
import com.axd.notificationService.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationLogService {

    private final NotificationLogRepository notificationLogRepository;


    //FIND NOTIFICATION BY ID
    public NotificationLog getNotificationById(Long id) {
        if (id == null) {
            throw new ValidationException("NOTIFICATION ID CANNOT BE NULL");
        }
        return notificationLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with id " + id + " does not exist"));

    }

    //FIND NOTIFICATION BY REQUEST ID
    public NotificationLog getNotificationByRequestId(String requestId) {
        if (requestId == null) {
            throw new ValidationException("REQUEST ID CANNOT BE NULL");
        }
        return notificationLogRepository.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with requestId " + requestId + " does not exist"));
    }

    //FIND NOTIFICATION BY USER ID
    public List<NotificationLog> getNotificationByUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("USER ID CANNOT BE NULL");
        } else {
            List<NotificationLog> notifications = notificationLogRepository.findByUserId(userId);

            if (notifications.isEmpty()) {
                throw new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with userId " + userId + " does not exist");
            }
            return notifications;
        }
    }

    public List<NotificationLog> getNotificationByType(String type) {
        if (type == null) {
            throw new ValidationException("TYPE CANNOT BE NULL");
        } else {
            List<NotificationLog> notifications = notificationLogRepository.findByType(type);
            if (notifications.isEmpty()) {
                throw new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with type " + type + " does not exist");
            }
            return notifications;
        }
    }

    public List<NotificationLog> getNotificationByState(String state) {
        if (state == null) {
            throw new ValidationException("STATE CANNOT BE NULL");
        } else {
            List<NotificationLog> notifications = notificationLogRepository.findByState(state);
            if (notifications.isEmpty()) {
                throw new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with state " + state + " does not exist");
            }
            return notifications;
        }
    }

    //FIND NOTIFICATION NY CHANNEL
    public List<NotificationLog> getNotificationByChannel(String channel) {
        if (channel == null) {
            throw new ValidationException("CHANNEL CANNOT BE NULL");
        } else {
            List<NotificationLog> notifications = notificationLogRepository.findByChannel(channel);
            if (notifications.isEmpty()) {
                throw new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with channel " + channel + " does not exist");
            }
            return notifications;
        }
    }

    //FIND NOTIFICATION NY PROVIDER
    public List<NotificationLog> getNotificationByProvider(String provider) {
        if (provider == null) {
            throw new ValidationException("PROVIDER CANNOT BE NULL");
        } else {
            List<NotificationLog> notifications = notificationLogRepository.findByProvider(provider);
            if (notifications.isEmpty()) {
                throw new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with provider " + provider + " does not exist");
            }
            return notifications;
        }
    }

    //FIND NOTIFICATION BY CHANNEL AND PROVIDER
    public List<NotificationLog> getNotificationByChannelAndProvider(String channel, String provider) {
        if (channel == null || provider == null) {
            throw new ValidationException("CHANNEL AND PROVIDER CANNOT BE NULL");
        } else {
            List<NotificationLog> notifications = notificationLogRepository.findByChannelAndProvider(channel, provider);
            if (notifications.isEmpty()) {
                throw new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with channel " + channel + " and provider " + provider + " does not exist");
            }
            return notifications;
        }
    }

    //FIND NOTIFICATION BY CHANNEL AND STATE
    public List<NotificationLog> getNotificationByChannelAndState(String channel, String state) {
        if (channel == null || state == null) {
            throw new ValidationException("CHANNEL AND STATE CANNOT BE NULL");
        } else {
            List<NotificationLog> notifications = notificationLogRepository.findByChannelAndState(channel, state);
            if (notifications.isEmpty()) {
                throw new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with channel " + channel + " and state " + state + " does not exist");
            }
            return notifications;
        }
    }

    //FIND NOTIFICATION BY NOTIFICATIONID
    public NotificationLog getNotificationByNotificationId(Long notificationId) {
        if (notificationId == null) {
            throw new ValidationException("NOTIFICATION ID CANNOT BE NULL");
        }
        return notificationLogRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("NOTIFICATION NOT FOUND\n" +
                        "Notification with notificationId " + notificationId + " does not exist"));
    }

    //LOG NOTIFICATION IN THE DATABASE
    public void logNotification(NotificationRequest request, NotificationSendResult result,
                                String notificationId, NotificationChannel channel) {
        NotificationLog log = new NotificationLog();
        log.setRequestId(request.getRequestId());
        log.setNotificationId(notificationId);
        log.setUserId(request.getUserId());
        log.setType(request.getType().toString());
        log.setChannel(channel.toString());
        log.setProvider(result.getProvider().toString());
        log.setProviderNotificationId(result.getExternalId());
        log.setState(result.getStatus().toString());

        notificationLogRepository.save(log);
    }

}
