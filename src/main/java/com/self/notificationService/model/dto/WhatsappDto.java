package com.self.notificationService.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WhatsappDto {
    @NotBlank(message = "Receiver phone number cannot be blank")
    private String receiverPhoneNumber;

    @NotBlank(message = "Sender phone number cannot be blank")
    private String from;

    @NotBlank(message = "Message cannot be blank")
    private String message;
}

