package com.example.ecommerce.inventory.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@ConfigurationProperties(prefix = "inventory")
@Getter
@Setter
public class InventoryClientConfig {

    private String baseUrl = "http://localhost:8081";
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 10000;

    @Bean
    public RestClient inventoryRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeoutMs);
        factory.setReadTimeout(readTimeoutMs);

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }
}
