package com.example.image_process_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.image_process_api.entity.Image;
import com.example.image_process_api.repository.ImageRepository;
import com.example.image_process_api.exception.AuthException;
import com.example.image_process_api.dto.PaginatedResponse;
import java.time.LocalDateTime;

@Service
public class ImageService {
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private S3Service s3Service;
    
    /**
     * Upload an image file to S3 and save metadata to MongoDB
     * @param fileData - byte array of the file
     * @param fileName - original filename
     * @param contentType - MIME type of the file
     * @param uploadedBy - user ID who uploaded the file (optional)
     * @return S3 file URL
     */
    public String uploadImage(byte[] fileData, String fileName, String contentType, String uploadedBy) {
        // Upload to S3 and validate file format
        String fileUrl = s3Service.uploadFile(fileData, fileName, contentType);
        
        // Save image metadata to MongoDB
        Image image = new Image(
                null,
                fileUrl,
                fileName,
                contentType,
                LocalDateTime.now(),
                uploadedBy
        );
        imageRepository.save(image);
        
        return fileUrl;
    }
    
    /**
     * Get the last uploaded image ID for tracking purposes
     * @return Last uploaded image ID
     */
    public String getLastUploadedImageId() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "uploadedAt"));
        Image lastImage = imageRepository.findAll(pageable)
                .getContent()
                .stream()
                .findFirst()
                .orElseThrow(() -> new AuthException("No images found"));
        return lastImage.getId();
    }
    
    /**
     * Get image by ID
     * @param imageId - Image document ID
     * @return Image details
     */
    public Image getImageById(String imageId) {
        return imageRepository.findById(imageId)
                .orElseThrow(() -> new AuthException("Image not found"));
    }
    
    /**
     * Get paginated list of images
     * @param page - page number (0-indexed)
     * @param limit - number of items per page
     * @return Paginated response with images
     */
    public PaginatedResponse<Image> getImages(int page, int limit) {
        // Validate pagination parameters
        if (page < 0) {
            page = 0;
        }
        if (limit <= 0 || limit > 100) {
            limit = 10;
        }
        
        Pageable pageable = PageRequest.of(page, limit);
        Page<Image> imagePage = imageRepository.findAll(pageable);
        
        return new PaginatedResponse<>(
                imagePage.getContent(),
                imagePage.getTotalElements(),
                imagePage.getTotalPages(),
                page,
                limit
        );
    }
    
    /**
     * Get images uploaded by a specific user
     * @param userId - user ID
     * @return List of images uploaded by user
     */
    public PaginatedResponse<Image> getImagesByUser(String userId, int page, int limit) {
        // Validate pagination parameters
        if (page < 0) {
            page = 0;
        }
        if (limit <= 0 || limit > 100) {
            limit = 10;
        }
        
        Pageable pageable = PageRequest.of(page, limit);
        Page<Image> imagePage = imageRepository.findByUploadedBy(userId, pageable);
        
        return new PaginatedResponse<>(
                imagePage.getContent(),
                imagePage.getTotalElements(),
                imagePage.getTotalPages(),
                page,
                limit
        );
    }
}
