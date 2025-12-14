package ru.practicum.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.ewm.client.StatsClient;

@Configuration
public class AppConfig {
    @Value("${stats-service.url}")
    private String statsServiceUrl;

    @Bean
    public StatsClient statsClient(RestTemplateBuilder restTemplateBuilder) {
        return new StatsClient(restTemplateBuilder, statsServiceUrl);
    }
}
