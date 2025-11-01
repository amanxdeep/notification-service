package com.self.notificationService.repository;

import com.self.notificationService.model.entity.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    List<NotificationLog> findByUserId(Long userId);
    Optional<NotificationLog> findByRequestId(String requestId);
}