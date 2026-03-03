package com.axd.notificationService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.axd.notificationService.model.ConfigKey;
import com.axd.notificationService.model.entity.ConfigEntity;
import com.axd.notificationService.repository.ConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigCacheService {

    private final ConfigRepository configRepository;
    private final ObjectMapper objectMapper;

    private volatile Map<String, String> cache = new HashMap<>();

    public String get(ConfigKey configKey) {
        return cache.get(buildCacheKey(configKey.getGroup(), configKey.getKey()));
    }

    public <T> T get(ConfigKey configKey, Class<T> objectClass) throws JsonProcessingException {
        String value = get(configKey);
        return objectMapper.readValue(value, objectClass);
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

    private String buildCacheKey(String group, String key) {
        return group + "." + key;
    }
}
