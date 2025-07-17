package com.center.clinicManagementSystem.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String PATIENTS_CACHE = "patients";
    public static final String DOCTORS_CACHE = "doctors";
    public static final String APPOINTMENTS_CACHE = "appointments";
    public static final String USER_DETAILS_CACHE = "userDetails";

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager();
        cacheManager.setCacheNames(Arrays.asList(
                PATIENTS_CACHE,
                DOCTORS_CACHE,
                APPOINTMENTS_CACHE,
                USER_DETAILS_CACHE
        ));
        return cacheManager;
    }
}
