package com.self.notificationService.provider.whatsAppProvider;

import com.self.notificationService.config.TwilioConfig;
import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.enums.NotificationRequestStatus;
import com.self.notificationService.model.dto.WhatsappDto;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;
import com.self.notificationService.provider.NotificationProviderService;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.Account;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TwilioWhatsAppProviderService implements NotificationProviderService {

    private final TwilioConfig twilioConfig;

    @Override
    public NotificationSendResult send(NotificationRequest request) {
        MDC.put(LogContextKey.PROVIDER.name(), getProviderType().name());

        NotificationSendResult result = new NotificationSendResult()
            .setProvider(getProviderType());

        try {
            WhatsappDto whatsappDto = buildWhatsAppDtoFromRequest(request);
            String messageSid = sendWhatsAppMessage(whatsappDto);

            result.setStatus(NotificationRequestStatus.SUCCESS)
                    .setExternalId(messageSid);
        } catch (Exception e) {
            log.error("Exception while sending WhatsApp message", e);

            result.setStatus(NotificationRequestStatus.FAILURE)
                    .setErrorMessage(e.getMessage());
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
            Account.fetcher().fetch();
            return true;
        } catch (ApiException e) {
            log.error("Twilio API error: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("Twilio not reachable: {}", e.getMessage(), e);
            return false;
        }
    }

    private String sendWhatsAppMessage(WhatsappDto whatsappDto) {
        Message message = Message.creator(
                new PhoneNumber(whatsappDto.getFrom()),
                new PhoneNumber(whatsappDto.getReceiverPhoneNumber()),
                whatsappDto.getMessage()
        ).create();

        return message.getSid();
    }

    private WhatsappDto buildWhatsAppDtoFromRequest(NotificationRequest request) {
        String phoneNumber = (String) request.getData().get("receiverPhoneNumber");
        phoneNumber = "whatsapp:" + phoneNumber;

        String message = (String) request.getData().get("message");

        return new WhatsappDto(
                twilioConfig.getWhatsappFromNumber(),
                phoneNumber,
                message
        );
    }
}

