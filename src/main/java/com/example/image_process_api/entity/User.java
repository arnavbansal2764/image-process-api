package com.example.image_process_api.entity;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Document("User")
@AllArgsConstructor
@Getter
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;
    private String password;
}