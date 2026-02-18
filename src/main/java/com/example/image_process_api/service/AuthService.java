package com.example.image_process_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.image_process_api.dto.RegisterRequest;
import com.example.image_process_api.dto.LoginRequest;
import com.example.image_process_api.dto.AuthResponse;
import com.example.image_process_api.entity.User;
import com.example.image_process_api.repository.UserRepository;
import com.example.image_process_api.security.JwtTokenProvider;
import com.example.image_process_api.exception.AuthException;
import java.util.Optional;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if username already exists
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new AuthException("Username already exists");
        }
        
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
    
    public AuthResponse login(LoginRequest loginRequest) {
        // Find user by username
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        
        if (!userOpt.isPresent()) {
            throw new AuthException("User not found");
        }
        
        User user = userOpt.get();
        
        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new AuthException("Invalid password");
        }
        
        // Generate JWT token
        String jwt = jwtTokenProvider.generateToken(user.getId(), user.getUsername());
        
        // Return user with JWT
        return new AuthResponse(user, jwt);
    }
}
