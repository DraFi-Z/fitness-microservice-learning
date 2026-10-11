package com.fitness.userservice.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KeycloakEventMessage {
    private String type;                 // e.g. "REGISTER"
    private String realmId;
    private String userId;               // Keycloak user id (same as JWT "sub")
    private Map<String, String> details; // email, first_name, last_name, username
}