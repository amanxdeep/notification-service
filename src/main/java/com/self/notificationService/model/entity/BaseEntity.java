package com.self.notificationService.model.entity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseEntity {

    //createAt represents the time when the entity is created
    private LocalDateTime createdAt;
    //updateAt represents the time when the entity is updated
    private LocalDateTime updatedAt;

    @PrePersist//this annotation means that this method will be called before the entity is persisted
    //this method will be called before the entity is persisted()
    //will set the createdAt and updatedAt to the current date and time automatically
    protected void onCreate() {
        createdAt = LocalDateTime.now();//.now() returns the current date and time
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate//this annotation means that this method will be called before the entity is updated

    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
