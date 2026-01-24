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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailChannelService implements NotificationChannelService {

    private final ProviderFactory providerFactory;
    private final ProviderSelectionService providerSelectionService;

    @Override
    public NotificationSendResult send(NotificationRequest request) {

        MDC.put(LogContextKey.CHANNEL.name(), getChannelType().name());
        
        log.info("Processing email notification request. RequestId: {}", request.getId());

        List<ProviderConfig> providers = providerSelectionService.getProviders(getConfigKey());

        if (providers.isEmpty()) {
            logger.error("No enabled email providers configured");
            return new NotificationSendResult()
                .setStatus(NotificationRequestStatus.FAILURE)
                .setErrorMessage(ErrorMessage.PROVIDER_NOT_AVAILABLE.getMessage());
        }

        log.debug("Available email providers: {} providers found", providers.size());

        for (ProviderConfig providerConfig : providers) {
            NotificationProvider provider = providerConfig.getProvider();

            MDC.put(LogContextKey.PROVIDER.name(), provider.name());

            log.debug("Attempting to send using provider: {} with rank: {}", provider.name(), providerConfig.getRank());
            
            NotificationProviderService providerService = providerFactory
                    .getProviderService(getChannelType(), provider);

            if (!ObjectUtils.isEmpty(providerService) && providerService.isAvailable()) {
                logger.debug("Provider {} is available, attempting to send", provider.name());
                NotificationSendResult result = providerService.send(request);
                
                if (result.getStatus() == NotificationRequestStatus.SUCCESS) {
                    logger.info("Email notification sent successfully using provider: {}", provider.name());
                    return result;
                }
                
                log.warn("Provider failed to send email, trying next provider. Error: {}", result.getErrorMessage());
            } else {
                log.warn("Provider is not available or not found");
            }
        }

        log.error("All email providers failed. Unable to send notification");
        return new NotificationSendResult()
            .setStatus(NotificationRequestStatus.FAILURE)
            .setErrorMessage(ErrorMessage.PROVIDER_NOT_AVAILABLE.getMessage());
    }

    @Override
    public NotificationChannel getChannelType() {
        return NotificationChannel.EMAIL;
    }

    @Override
    public ConfigKey getConfigKey() {
        return ConfigKey.EMAIL_PROVIDER_CONFIG;
    }


}