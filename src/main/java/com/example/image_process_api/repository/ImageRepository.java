package com.example.image_process_api.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.example.image_process_api.entity.Image;
import java.util.List;

@Repository
public interface ImageRepository extends MongoRepository<Image, String> {
    List<Image> findByUploadedBy(String uploadedBy);
    Page<Image> findByUploadedBy(String uploadedBy, Pageable pageable);
}
