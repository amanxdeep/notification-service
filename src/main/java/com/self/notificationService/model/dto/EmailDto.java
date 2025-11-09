package com.self.notificationService.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDto {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Receiver email is required")
    private String receiverAddress;

    @NotBlank(message = "Email subject is required")
    private String subject;

    @NotBlank(message = "Email body is required")
    private String body;

    @NotBlank(message = "Email sender address is required")
    private String fromAddress;

    @NotBlank(message = "Email sender name is required")
    private String fromName;
}
