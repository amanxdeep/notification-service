package com.self.notificationService.repository;

import com.self.notificationService.model.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByUserId(Long userId);
    Optional<NotificationLog> findByRequestId(String requestId);
    List<NotificationLog> findByType(String type);
    List<NotificationLog> findByState(String state);
    List<NotificationLog> findByNotificationId(Long notificationId);
    List<NotificationLog> findByNotificationIdAndState(Long notificationId, String state);
    List<NotificationLog> findByChannel(String channel);
    List<NotificationLog> findByProvider(String provider);
    List<NotificationLog> findByChannelAndState(String channel, String state);
    List<NotificationLog> findByChannelAndProvider(String channel, String provider);
}