package com.axd.notificationService.model;


import com.axd.notificationService.enums.NotificationProvider;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProviderConfig {
    private NotificationProvider provider;
    private boolean enabled;
    private int rank;
}