package com.example.image_process_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.image_process_api.dto.RegisterRequest;
import com.example.image_process_api.dto.AuthResponse;
import com.example.image_process_api.entity.User;
import com.example.image_process_api.repository.UserRepository;
import com.example.image_process_api.security.JwtTokenProvider;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public AuthResponse register(RegisterRequest registerRequest) {
        // Create new user
        User user = new User(
            null,
            registerRequest.getUsername(),
            passwordEncoder.encode(registerRequest.getPassword())
        );
        
        // Save user to database
        User savedUser = userRepository.save(user);
        
        // Generate JWT token
        String jwt = jwtTokenProvider.generateToken(savedUser.getId(), savedUser.getUsername());
        
        // Return user with JWT
        return new AuthResponse(savedUser, jwt);
    }
}
