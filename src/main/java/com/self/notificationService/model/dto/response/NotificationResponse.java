package com.self.notificationService.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;

@Data
@AllArgsConstructor
public class NotificationResponse {

    private String id;
    private String status;
}
