package com.example.image_process_api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.image_process_api.dto.RegisterRequest;
import com.example.image_process_api.dto.AuthResponse;
import com.example.image_process_api.service.AuthService;


@Controller
@RestController
@RequestMapping("/")
public class main {
    
    @Autowired
    private AuthService authService;
    
    @GetMapping()
    public String getHomeString() {
        return new String("Welcome to Image Processing API");
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
    
}
