package com.self.notificationService.provider.emailProvider;
import com.self.notificationService.config.AwsSesConfig;
import com.self.notificationService.constants.AppConstants;
import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.enums.NotificationRequestStatus;
import com.self.notificationService.model.dto.EmailDto;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;
import com.self.notificationService.provider.NotificationProviderService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;


@Service
@RequiredArgsConstructor
public class AwsEmailProviderService implements NotificationProviderService {

    private final SesClient sesClient;
    private final AwsSesConfig awsSesConfig;

    private String sendEmail(EmailDto emailDto) {
        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .destination(Destination.builder()
                        .toAddresses(emailDto.getReceiverAddress())
                        .build())
                .message(Message.builder()
                        .subject(Content.builder()
                                .charset(AppConstants.UTF_8)
                                .data(emailDto.getSubject())
                                .build())
                        .body(Body.builder()
                                .html(Content.builder()
                                        .charset(AppConstants.UTF_8)
                                        .data(emailDto.getBody())
                                        .build())
                                .build())
                        .build())
                .source(emailDto.getFromAddress())
                .build();

        SendEmailResponse response = sesClient.sendEmail(emailRequest);
        return response.messageId();
    }

    @Override
    public NotificationSendResult send(NotificationRequest request) {
        NotificationSendResult result = new NotificationSendResult();
        result.setProvider(NotificationProvider.AWS_SES);

        try {
            EmailDto emailDto = buildEmailFromRequest(request);
            String messageId = sendEmail(emailDto);

            result.setStatus(NotificationRequestStatus.SUCCESS);
            result.setExternalId(messageId);
        } catch (Exception e) {
            result.setStatus(NotificationRequestStatus.FAILURE);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }


    private EmailDto buildEmailFromRequest(NotificationRequest request) {
        String to = (String) request.getData().get(AppConstants.EMAIL);
        String subject = (String) request.getData().get(AppConstants.SUBJECT);
        String body = (String) request.getData().get(AppConstants.BODY);

        return EmailDto.builder()
                .receiverAddress(to)
                .subject(subject)
                .body(body)
                .fromAddress(awsSesConfig.getFromEmail())
                .fromName(awsSesConfig.getFromName())
                .build();
    }

    @Override
    public NotificationProvider getProviderType() {
        return NotificationProvider.AWS_SES;
    }

    @Override
    public Boolean isAvailable() {
        try {
            sesClient.getSendQuota();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
