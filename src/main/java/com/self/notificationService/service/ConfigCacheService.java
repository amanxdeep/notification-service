package com.self.notificationService.service;

import com.self.notificationService.model.ConfigKey;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigCacheService {

    private final ConfigRepository configRepository;
    private final ObjectMappper objectMapper;


    private volatile Map<String, String> cache = new HashMap<>();


    public String get(ConfigKey configKey) {
        return cache.get(buildCacheKey(configKey));
    }


    public <T> T get(ConfigKey configKey, Class<T> objectClass) {
        String value = get(configKey);
        T object = objectMapper.readValue(value, objectClass);
        return object;
    }

    public synchronized void refresh() {
        log.info("Refreshing config cache...");

        Map<String, String> freshCache = new HashMap<>();

        List<ConfigEntity> configs = configRepository.findAll();
        for (ConfigEntity config : configs) {
            String cacheKey = buildCacheKey(config.getGroup(), config.getKey());
            freshCache.put(cacheKey, config.getValue());
        }


        cache = freshCache;

        log.info("Config cache refreshed. Total entries: {}", cache.size());
    }

    private String buildCacheKey(ConfigKey configKey) {
        return configKey.getGroup() + "." + configKey.getKey();
    }
}
