package com.self.notificationService.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class NotificationLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String requestId;

    private String notificationId;
    private Long userId;
    private String eventId;
    private String type;
    private String channel;
    private String provider;
    private String providerNotificationId;
    private String state;

}