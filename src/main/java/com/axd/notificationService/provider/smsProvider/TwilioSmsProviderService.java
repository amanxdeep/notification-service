package com.axd.notificationService.provider.smsProvider;

import com.axd.notificationService.constants.AppConstants;
import com.axd.notificationService.enums.LogContextKey;
import com.axd.notificationService.enums.NotificationProvider;
import com.axd.notificationService.enums.NotificationRequestStatus;
import com.axd.notificationService.model.dto.SmsDto;
import com.axd.notificationService.model.dto.request.NotificationRequest;
import com.axd.notificationService.model.dto.response.NotificationSendResult;
import com.axd.notificationService.provider.NotificationProviderService;
import com.twilio.exception.ApiException;
import com.twilio.rest.api.v2010.Account;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TwilioSmsProviderService implements NotificationProviderService {

    @Value("${twilio.phone-number}")
    private String fromNumber;

    @Override
    public NotificationSendResult send(NotificationRequest request) {
        MDC.put(LogContextKey.PROVIDER.name(), getProviderType().name());

        NotificationSendResult notificationSendResult = new NotificationSendResult()
                .setProvider(getProviderType());

        try {
            SmsDto smsDto = buildSmsDtoFromRequest(request);
            String messageId = sendSms(smsDto.getReceiverPhoneNumber(), smsDto.getMessageToSend());

            notificationSendResult.setStatus(NotificationRequestStatus.SUCCESS)
                    .setExternalId(messageId);
        } catch (Exception e) {
            log.error("Exception while sending SMS message", e);

            notificationSendResult.setStatus(NotificationRequestStatus.FAILURE)
                    .setErrorMessage(e.getMessage());
        }

        return notificationSendResult;
    }

    @Override
    public NotificationProvider getProviderType() {
        return NotificationProvider.TWILIO_SMS;
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

    private SmsDto buildSmsDtoFromRequest(NotificationRequest request) {
        String phoneNumber = (String) request.getData().get(AppConstants.RECEIVER_PHONE_NUMBER);
        String message = (String) request.getData().get(AppConstants.MESSAGE);
        return SmsDto.builder()
                .receiverPhoneNumber(phoneNumber)
                .messageToSend(message)
                .build();
    }

    private String sendSms(String phoneNumber, String messageBody) {
        Message message = Message.creator(
                new PhoneNumber(phoneNumber),
                new PhoneNumber(fromNumber),
                messageBody
        ).create();

        return message.getSid();
    }
}
