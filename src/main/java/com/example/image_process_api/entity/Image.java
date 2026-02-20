package com.example.image_process_api.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Document("images")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    private String id;
    
    private String fileUrl;
    private String fileName;
    private String contentType;
    private LocalDateTime uploadedAt;
    private String uploadedBy;
}
