package com.self.notificationService.provider.smsProvider;

import com.self.notificationService.constants.AppConstants;
import com.self.notificationService.enums.LogContextKey;
import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.enums.NotificationRequestStatus;
import com.self.notificationService.model.dto.SmsDto;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;
import com.self.notificationService.provider.NotificationProviderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsSmsProviderService implements NotificationProviderService {

    private final SnsClient snsClient;

    private String sendSms(String phoneNumber, String message) {
        PublishRequest publishRequest = PublishRequest.builder()
                .phoneNumber(phoneNumber)
                .message(message)
                .build();

        PublishResponse publishResponse = snsClient.publish(publishRequest);
        return publishResponse.messageId();
    }

    private SmsDto buildSmsDtoFromRequest(NotificationRequest request) {
        String phoneNumber = (String) request.getData().get(AppConstants.RECEIVER_PHONE_NUMBER);
        String message = (String) request.getData().get(AppConstants.MESSAGE);
        return SmsDto.builder()
                .receiverPhoneNumber(phoneNumber)
                .messageToSend(message)
                .build();
    }


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
            log.error("Exception while sending SMS", e);
            notificationSendResult.setStatus(NotificationRequestStatus.FAILURE)
                    .setErrorMessage(e.getMessage());
        }

        return notificationSendResult;
    }

    @Override
    public NotificationProvider getProviderType() {
        return NotificationProvider.AWS_SNS;
    }

    @Override
    public Boolean isAvailable() {
        try {
            snsClient.getSMSAttributes();
            return true;
        } catch (Exception e) {
            log.error("Exception while checking availability", e);
            return false;
        }
    }
}
