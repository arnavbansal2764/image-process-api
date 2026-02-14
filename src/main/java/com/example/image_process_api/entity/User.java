package com.example.image_process_api.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Document("User")
@AllArgsConstructor
@Getter
public class User {
    @Id
    private String id;

    private String username;
    private String password;
}