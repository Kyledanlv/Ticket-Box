package com.example.ticketboxanalytics.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)); // Mặc định 10 phút

        // Cấu hình TTL riêng cho từng Cache Store
        Map<String, RedisCacheConfiguration> cacheConfigs = Map.of(
                "revenue-data", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)),
                "sales-data", defaultCacheConfig.entryTtl(Duration.ofMinutes(10)),
                "kpi-data", defaultCacheConfig.entryTtl(Duration.ofMinutes(3))
        );

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }
}