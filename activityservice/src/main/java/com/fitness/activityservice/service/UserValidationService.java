package com.fitness.activityservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {
    private final WebClient userServiceWebClient;

    public Boolean validateUser(String userId){
        log.info("Calling User Validation API from Activity-Service for userID {} :",userId);
        try{
            return userServiceWebClient.get()
                    .uri("api/{userId}/validate",userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        }catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("User Not Found: " + userId, e);
            } else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new RuntimeException("Invalid Request for userId: " + userId, e);
            }
            throw new RuntimeException("Unexpected error validating userId: " + userId, e);
        } catch (WebClientRequestException e) {
            throw new RuntimeException("Could not reach user service for userId: " + userId, e);
        }
    }
}
