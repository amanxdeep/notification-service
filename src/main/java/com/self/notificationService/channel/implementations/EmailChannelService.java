package com.self.notificationService.channel.implementations;

import com.self.notificationService.channel.NotificationChannelService;
import com.self.notificationService.enums.NotificationChannel;
import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.enums.NotificationRequestStatus;
import com.self.notificationService.factory.ProviderFactory;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;

import com.self.notificationService.provider.NotificationProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailChannelService implements NotificationChannelService {

    private final ProviderFactory providerFactory;

    @Override
    public NotificationSendResult send(NotificationRequest request) {
        NotificationProvider provider = NotificationProvider.AWS_SES;

        NotificationProviderService providerService = providerFactory
                .getProviderService(getChannelType(), provider);

        if (providerService == null || !providerService.isAvailable()) {
            NotificationSendResult result = new NotificationSendResult();
            result.setStatus(NotificationRequestStatus.FAILURE);
            result.setErrorMessage("Email provider not available");
            return result;
        }

        return providerService.send(request);
    }

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.EMAIL;
    }


}