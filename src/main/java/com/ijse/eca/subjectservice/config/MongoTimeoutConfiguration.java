package com.ijse.eca.subjectservice.config;

import com.mongodb.MongoClientSettings;
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class MongoTimeoutConfiguration {

    @Bean
    MongoClientSettingsBuilderCustomizer mongoTimeoutCustomizer() {
        return (MongoClientSettings.Builder builder) -> builder
                .applyToClusterSettings(settings -> settings.serverSelectionTimeout(2, TimeUnit.SECONDS))
                .applyToSocketSettings(settings -> settings.connectTimeout(2, TimeUnit.SECONDS).readTimeout(2, TimeUnit.SECONDS))
                .applyToConnectionPoolSettings(settings -> settings.maxWaitTime(2, TimeUnit.SECONDS));
    }
}
