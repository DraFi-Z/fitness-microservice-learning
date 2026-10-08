package com.fitness.userservice.repository;

import com.fitness.userservice.DTO.UserResponseDTO;
import com.fitness.userservice.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {
    boolean existsByEmail(@NotBlank(message = "Email is Required") @Email(message = "Invalid Email") String email);

    Boolean existsByKeyCloakId(String userId);

    User findByEmail(@NotBlank(message = "Email is Required") @Email(message = "Invalid Email") String email);
}
