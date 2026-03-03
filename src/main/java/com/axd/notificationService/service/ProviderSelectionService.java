package com.axd.notificationService.service;

import com.axd.notificationService.model.ChannelProviderConfig;
import com.axd.notificationService.model.ConfigKey;
import com.axd.notificationService.model.ProviderConfig;
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
     *
     * @param configKey Configuration key for the channel
     * @return List of providers sorted by rank
     */
    public List<ProviderConfig> getProviders(ConfigKey configKey) {
        try {
            logger.debug("Fetching provider config using ConfigKey: {}", configKey.name());

            ChannelProviderConfig channelConfig = configCacheService.get(configKey, ChannelProviderConfig.class);

            if (channelConfig == null || channelConfig.getProviders() == null) {
                logger.warn("No configuration found with ConfigKey: {}", configKey.name());
                return List.of();
            }

            return channelConfig.getProviders().stream()
                    .filter(ProviderConfig::isEnabled)
                    .sorted((p1, p2) -> Integer.compare(p1.getRank(), p2.getRank()))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("Error fetching provider configuration", e);
            return List.of();
        }
    }

}
