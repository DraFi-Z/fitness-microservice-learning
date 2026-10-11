package com.fitness.userservice.service;

import com.fitness.userservice.DTO.KeycloakUserEvent;
import com.fitness.userservice.DTO.RegisterRequestDTO;
import com.fitness.userservice.DTO.UserResponseDTO;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private UserRepository userRepository;

    public UserResponseDTO register(@Valid RegisterRequestDTO request) {

        if(userRepository.existsByEmail(request.getEmail())){
            User existingUser = userRepository.findByEmail(request.getEmail());
            UserResponseDTO userResponse = new UserResponseDTO();
            userResponse.setId(existingUser.getId());
            userResponse.setPassword(existingUser.getPassword());
            userResponse.setFirstName(existingUser.getFirstName());
            userResponse.setKeyCloakId(existingUser.getKeyCloakId());
            userResponse.setLastName(existingUser.getLastName());
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setCreatedAt(existingUser.getCreatedAt());
            userResponse.setUpdatedAt(existingUser.getCreatedAt());

            return userResponse;
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setKeyCloakId(request.getKeyCloakId());

        User savedUser = userRepository.save(user);
        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setFirstName(savedUser.getFirstName());
        userResponse.setLastName(savedUser.getLastName());
        userResponse.setEmail(savedUser.getEmail());

        return userResponse;
    }

    public UserResponseDTO getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User Not Found"));

        UserResponseDTO userResponse = new UserResponseDTO();
        userResponse.setId(user.getId());
        userResponse.setPassword(user.getPassword());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setKeyCloakId(user.getKeyCloakId());
        userResponse.setLastName(user.getLastName());
        userResponse.setEmail(user.getEmail());
        userResponse.setCreatedAt(user.getCreatedAt());
        userResponse.setUpdatedAt(user.getCreatedAt());

        return userResponse;
    }

    public Boolean existByUserId(String userId) {
        log.info("Calling User Validation API for userID {} :",userId);
        return userRepository.existsByKeyCloakId(userId);
    }

    @Transactional
    public void syncFromKeycloak(KeycloakUserEvent event) {
        if (userRepository.findByKeyCloakId(event.keycloakId()).isPresent()) {
            log.info("Keycloak user {} already synced, skipping", event.keycloakId());
            return;
        }
        // A user may already exist locally (e.g. created via /register) without a Keycloak id: link them
        User existing = userRepository.findByEmail(event.email());
        if (existing != null) {
            existing.setKeyCloakId(event.keycloakId());
            userRepository.save(existing);
            return;
        }
        User user = new User();
        user.setKeyCloakId(event.keycloakId());
        user.setEmail(event.email());
        user.setFirstName(event.firstName());
        user.setLastName(event.lastName());
        userRepository.save(user);
    }

}
