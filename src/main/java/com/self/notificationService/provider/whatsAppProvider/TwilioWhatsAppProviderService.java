package com.self.notificationService.provider.whatsAppProvider;

import com.self.notificationService.config.TwilioWhatsAppConfig;
import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.enums.NotificationRequestStatus;
import com.self.notificationService.model.dto.WhatsappDto;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;
import com.self.notificationService.provider.NotificationProviderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwilioWhatsAppProviderService implements NotificationProviderService {

    private final TwilioWhatsAppConfig twilioConfig;
    private final WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Twilio API endpoint for sending messages
    private static final String TWILIO_API_URL = "https://api.twilio.com/2010-04-01/Accounts/";

    private String sendWhatsAppMessage(WhatsappDto whatsappDto) {
        String apiUrl = TWILIO_API_URL + twilioConfig.getAccountSid() + "/Messages.json";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("From", "whatsapp:" + twilioConfig.getWhatsappFromNumber());
        formData.add("To", "whatsapp:" + whatsappDto.getReceiverPhoneNumber());
        formData.add("Body", whatsappDto.getMessage());

        try {
            String response = webClient.post()
                    .uri(apiUrl)
                    .headers(headers -> headers.setBasicAuth(twilioConfig.getAccountSid(), twilioConfig.getAuthToken()))
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode jsonResponse = objectMapper.readTree(response);
            String messageSid = jsonResponse.path("sid").asText();

            if (messageSid == null || messageSid.trim().isEmpty()) {
                log.error("No SID in Twilio response: {}", response);
                throw new RuntimeException("No SID received from Twilio");
            }

            log.info("Message sent successfully, SID: {}", messageSid);
            return messageSid;

        } catch (Exception e) {
            log.error("Error sending WhatsApp message: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send WhatsApp message: " + e.getMessage(), e);
        }
    }

    private WhatsappDto buildWhatsAppDtoFromRequest(NotificationRequest request) {
        if (request == null || request.getData() == null) {
            throw new IllegalArgumentException("Notification request and data cannot be null");
        }

        String phoneNumber = Objects.toString(request.getData().get("receiverPhoneNumber"), "");
        String message = Objects.toString(request.getData().get("message"), "");


        return new WhatsappDto(
                twilioConfig.getWhatsappFromNumber(),
                phoneNumber,
                message
        );
    }

    @Override
    public NotificationSendResult send(NotificationRequest request) {
        NotificationSendResult result = new NotificationSendResult();
        result.setProvider(getProviderType());

        try {
            WhatsappDto whatsappDto = buildWhatsAppDtoFromRequest(request);
            String messageSid = sendWhatsAppMessage(whatsappDto);
            result.setStatus(NotificationRequestStatus.SUCCESS);
            result.setExternalId(messageSid);
        } catch (Exception e) {
            log.error("Error sending WhatsApp message: {}", e.getMessage(), e);
            result.setStatus(NotificationRequestStatus.FAILURE);
            result.setErrorMessage(e.getMessage());
        }

        return result;
    }

    @Override
    public NotificationProvider getProviderType() {
        return NotificationProvider.TWILIO_WHATSAPP;
    }

    @Override
    public Boolean isAvailable() {
        try {
            // Simple check that doesn't require API call
            return twilioConfig != null
                    && twilioConfig.getAccountSid() != null
                    && !twilioConfig.getAccountSid().trim().isEmpty()
                    && twilioConfig.getAuthToken() != null
                    && !twilioConfig.getAuthToken().trim().isEmpty()
                    && twilioConfig.getWhatsappFromNumber() != null
                    && !twilioConfig.getWhatsappFromNumber().trim().isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
}

