package com.self.notificationService.model.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class NotificationTemplate extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;       // ORDER_PLACED, PASSWORD_RESET, etc.
    private String channel;    // EMAIL, SMS
    private String subject;
    @Column(length = 2000)
    private String bodyTemplate;
}