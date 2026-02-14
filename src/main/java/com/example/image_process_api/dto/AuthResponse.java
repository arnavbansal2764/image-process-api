package com.example.image_process_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.example.image_process_api.entity.User;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private User user;
    private String jwt;
}
