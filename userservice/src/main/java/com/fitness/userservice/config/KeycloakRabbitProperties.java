package com.fitness.userservice.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@EnableConfigurationProperties(KeycloakRabbitProperties.class)
@ConfigurationProperties(prefix = "rabbitmq.keycloak")
@Validated
public record KeycloakRabbitProperties(
        @NotBlank String exchange,
        @NotBlank String routingKey,
        @NotBlank String queue,
        @NotBlank String dlx,
        @NotBlank String dlq
) {}