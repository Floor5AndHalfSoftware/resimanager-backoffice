package com.resimanager.backoffice.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

import static com.resimanager.backoffice.utils.Constants.LOGIN_ATTEMPTS_CACHE;

@Configuration
@EnableCaching
public class CacheConfig {

    @Primary
    @Bean(name = "cacheManagerLogin")
    public CacheManager cacheManagerLogin() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(LOGIN_ATTEMPTS_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(100));
        return cacheManager;
    }
}
