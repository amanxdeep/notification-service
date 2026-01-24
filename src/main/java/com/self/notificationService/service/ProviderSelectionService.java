package com.self.notificationService.service;

import com.self.notificationService.enums.NotificationChannel;
import com.self.notificationService.model.ChannelProviderConfig;
import com.self.notificationService.model.ConfigKey;
import com.self.notificationService.model.ProviderConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderSelectionService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderSelectionService.class);
    private final ConfigCacheService configCacheService;

    /**
     * Select providers for a channel based on cached configuration, sorted by rank (enabled providers first)
     * @param channel Notification channel type
     * @return List of providers sorted by rank
     */
    public List<ProviderConfig> selectProviders(NotificationChannel channel) {
        try {
            ConfigKey configKey = getConfigKeyForChannel(channel);
            logger.debug("Fetching provider config for channel: {} using ConfigKey: {}", channel.name(), configKey.name());

            ChannelProviderConfig channelConfig = configCacheService.get(configKey, ChannelProviderConfig.class);

            if (channelConfig == null || channelConfig.getProviders() == null) {
                logger.warn("No configuration found for channel: {} with ConfigKey: {}", channel.name(), configKey.name());
                return List.of();
            }

            return channelConfig.getProviders().stream()
                    .filter(ProviderConfig::isEnabled)
                    .sorted((p1, p2) -> Integer.compare(p1.getRank(), p2.getRank()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching provider configuration for channel: {}", channel.name(), e);
            return List.of();
        }
    }

    /**
     * Get primary provider (highest priority/lowest rank) for a channel
     * @param channel Notification channel type
     * @return Primary provider config or null if none available
     */
    public ProviderConfig getPrimaryProvider(NotificationChannel channel) {
        List<ProviderConfig> providers = selectProviders(channel);
        return providers.isEmpty() ? null : providers.get(0);
    }

    /**
     * Map NotificationChannel to corresponding ConfigKey
     */
    private ConfigKey getConfigKeyForChannel(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> ConfigKey.EMAIL_PROVIDER_CONFIG;
            case SMS -> ConfigKey.SMS_PROVIDER_CONFIG;
            case WHATSAPP -> ConfigKey.WHATSAPP_PROVIDER_CONFIG;
            default -> throw new IllegalArgumentException("Unsupported channel: " + channel);
        };
    }
}
