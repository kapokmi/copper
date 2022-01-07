package com.michelkapoko.copper;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;

@Configuration
@EnableScheduling
@ComponentScan("com.michelkapoko.copper")
public class BitmexIntegrationConfiguration {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
