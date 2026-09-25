package com.fitness.userservice.controller;


import com.fitness.userservice.DTO.RegisterRequestDTO;
import com.fitness.userservice.DTO.UserResponseDTO;
import com.fitness.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserProfile(@PathVariable(value="userId") String userId){
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request){
        return ResponseEntity.ok(userService.register(request));
    }

    @GetMapping("/{userId}/validate")
    public ResponseEntity<Boolean> validateUser(@PathVariable(value="userId") String userId){
        return ResponseEntity.ok(userService.existByUserId(userId));
    }

}
