package com.self.notificationService.channel.implementations;

import com.self.notificationService.channel.NotificationChannelService;
import com.self.notificationService.enums.ErrorMessage;
import com.self.notificationService.enums.LogContextKey;
import com.self.notificationService.enums.NotificationChannel;
import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.enums.NotificationRequestStatus;
import com.self.notificationService.factory.ProviderFactory;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;

import com.self.notificationService.provider.NotificationProviderService;
import lombok.RequiredArgsConstructor;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class EmailChannelService implements NotificationChannelService {

    private final ProviderFactory providerFactory;

    @Override
    public NotificationSendResult send(NotificationRequest request) {

        MDC.put(LogContextKey.CHANNEL.name(), getChannelType().name());

        NotificationProvider provider = NotificationProvider.AWS_SES;

        NotificationProviderService providerService = providerFactory
                .getProviderService(getChannelType(), provider);

        if (ObjectUtils.isEmpty(providerService) || !providerService.isAvailable()) {

            return new NotificationSendResult()
                .setStatus(NotificationRequestStatus.FAILURE)
                .setErrorMessage(ErrorMessage.PROVIDER_NOT_AVAILABLE.getMessage());
        }

        return providerService.send(request);
    }

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.EMAIL;
    }


}