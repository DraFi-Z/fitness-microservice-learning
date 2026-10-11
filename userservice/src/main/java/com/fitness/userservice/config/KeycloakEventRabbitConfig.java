package com.fitness.userservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KeycloakRabbitProperties.class)
@RequiredArgsConstructor
public class KeycloakEventRabbitConfig {

    private final KeycloakRabbitProperties props;

    @Bean
    public Queue userKeycloakQueue() {
        return QueueBuilder.durable(props.queue())
                .deadLetterExchange(props.dlx())
                .deadLetterRoutingKey(props.dlq())
                .build();
    }

    @Bean
    public Queue userKeycloakDlq() {
        return QueueBuilder.durable(props.dlq()).build();
    }

    @Bean
    public DirectExchange userKeycloakDlx() {
        return new DirectExchange(props.dlx());
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(userKeycloakDlq()).to(userKeycloakDlx()).with(props.dlq());
    }

    @Bean
    public Binding keycloakBinding() {
        return new Binding(props.queue(), Binding.DestinationType.QUEUE,
                props.exchange(), props.routingKey(), null);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }
}