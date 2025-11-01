package com.self.notificationService.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class NotificationLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String requestId;

    private String notificationId;
    private Long userId;
    private String eventId;
    private String type;
    private String channel;
    private String state;
    private LocalDateTime createdAt = LocalDateTime.now();
}