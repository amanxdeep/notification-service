package com.self.notificationService.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailDto {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Required email is required")
    private String receiverAddress;

    @NotBlank(message = "Required subject is required")
    private String subject;

    @NotBlank(message = "Required body is required")
    private String body;

    @NotBlank(message = "Required from is required")
    private String fromAddress;

    private String fromName;
}
