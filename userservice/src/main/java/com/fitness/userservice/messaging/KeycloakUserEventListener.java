package com.fitness.userservice.messaging;

import com.fitness.userservice.DTO.KeycloakEventMessage;
import com.fitness.userservice.DTO.KeycloakUserEvent;
import com.fitness.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeycloakUserEventListener {

    private final UserService userService;

    @RabbitListener(queues = "${rabbitmq.keycloak.queue}")
    public void onMessage(KeycloakEventMessage message) {
        if (!"REGISTER".equals(message.getType())) {
            log.debug("Ignoring Keycloak event type {}", message.getType());
            return;
        }
        Map<String, String> d = message.getDetails() == null ? Map.of() : message.getDetails();
        String firstName = d.getOrDefault("first_name", d.get("username"));

        if (!StringUtils.hasText(message.getUserId())
                || !StringUtils.hasText(d.get("email"))
                || !StringUtils.hasText(firstName)) {
            throw new AmqpRejectAndDontRequeueException("Invalid REGISTER event, missing userId/email/name");
        }

        KeycloakUserEvent event = new KeycloakUserEvent(
                message.getUserId(), d.get("email"), firstName, d.get("last_name"));
        try {
            userService.syncFromKeycloak(event);
        } catch (DataIntegrityViolationException e) {
            log.info("Concurrent duplicate for {}, treating as already synced", event.keycloakId());
        }
    }
}