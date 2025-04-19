package com.reliaquest.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate employeeRestTemplate(RestTemplateBuilder restTemplateBuilder, @Value("${mock.server.url}") String BASE_URL) {
        return restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(BASE_URL))
                .build();
    }
}
