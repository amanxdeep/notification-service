package com.self.notificationService.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmsDto {

    @Size(min = 10,message = "Phone number must be at least 10 digits" )
    @Pattern(regexp = "^\\d{10,}$", message = "Phone number must be a valid 10-digit number")
    @NotBlank(message = "Sms receiver phone number cannot be blank")
    private String receiverPhoneNumber;

    @NotBlank(message = "Sms message is required")
    private String messageToSend;
}
