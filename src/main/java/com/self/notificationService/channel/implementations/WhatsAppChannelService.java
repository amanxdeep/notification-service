package com.self.notificationService.channel.implementations;

import com.self.notificationService.channel.NotificationChannelService;
import com.self.notificationService.enums.ErrorMessage;
import com.self.notificationService.enums.LogContextKey;
import com.self.notificationService.enums.NotificationChannel;
import com.self.notificationService.enums.NotificationProvider;
import com.self.notificationService.enums.NotificationRequestStatus;
import com.self.notificationService.factory.ProviderFactory;
import com.self.notificationService.model.ConfigKey;
import com.self.notificationService.model.ProviderConfig;
import com.self.notificationService.model.dto.request.NotificationRequest;
import com.self.notificationService.model.dto.response.NotificationSendResult;
import com.self.notificationService.provider.NotificationProviderService;


import com.self.notificationService.service.ProviderSelectionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class WhatsAppChannelService implements NotificationChannelService {

    private final ProviderFactory providerFactory;
    private final ProviderSelectionService providerSelectionService;

    @Override
    public NotificationSendResult send(NotificationRequest request) {
        MDC.put(LogContextKey.CHANNEL.name(), getChannelType().name());
        
        log.info("Processing email notification request.");

        List<ProviderConfig> providers = providerSelectionService.getProviders(getConfigKey());

        if (providers.isEmpty()) {
            log.error("No email providers configured");
            return new NotificationSendResult()
                .setStatus(NotificationRequestStatus.FAILURE)
                .setErrorMessage(ErrorMessage.PROVIDER_NOT_AVAILABLE.getMessage());
        }

        return sendWithFallback(request, providers);
    }

    private NotificationSendResult sendWithFallback(NotificationRequest request, List<ProviderConfig> providers) {
        log.debug("Available email providers: {} providers found", providers.size());

        for (ProviderConfig providerConfig : providers) {
            NotificationProvider provider = providerConfig.getProvider();

            MDC.put(LogContextKey.PROVIDER.name(), provider.name());

            log.debug("Attempting to send with provider rank: {}", provider.name(), providerConfig.getRank());
            
            NotificationProviderService providerService = providerFactory
                    .getProviderService(getChannelType(), provider);

            if (ObjectUtils.isEmpty(providerService) || !providerService.isAvailable()) {
                log.warn("Provider is not available or not found");
                continue;
            }

            log.debug("Provider is available, attempting to send");
            NotificationSendResult result = providerService.send(request);
            
            if (result.getStatus() == NotificationRequestStatus.SUCCESS) {
                log.info("Notification sent successfully");
                return result;
            }
            
            log.warn("Provider failed to send email, trying next provider. Error: {}", result.getErrorMessage());
        }

        log.error("All email providers failed. Unable to send notification");

        return new NotificationSendResult()
            .setStatus(NotificationRequestStatus.FAILURE)
            .setErrorMessage(ErrorMessage.PROVIDER_NOT_AVAILABLE.getMessage());
    }

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.WHATSAPP;
    }

    @Override
    public ConfigKey getConfigKey() {
        return ConfigKey.WHATSAPP_PROVIDER_CONFIG;
    }
}
