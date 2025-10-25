package com.self.notificationService.model.dto.response;

import lombok.Data;

@Data
public class GenericError {
    private Integer code;
    private String message;
}
