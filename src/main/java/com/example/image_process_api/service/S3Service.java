package com.example.image_process_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import com.example.image_process_api.exception.AuthException;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

@Service
public class S3Service {
    
    @Autowired
    private S3Client s3Client;
    
    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    
    @Value("${aws.s3.region}")
    private String region;
    
    // Allowed file extensions
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".png", ".jpg", ".jpeg");
    
    // Allowed MIME types
    private static final List<String> ALLOWED_MIME_TYPES = Arrays.asList(
            "image/png",
            "image/jpeg",
            "image/jpg"
    );
    
    /**
     * Upload a file to S3
     * @param fileData - byte array of the file
     * @param fileName - original filename
     * @param contentType - MIME type of the file
     * @return S3 file URL
     */
    public String uploadFile(byte[] fileData, String fileName, String contentType) {
        // Validate file format
        validateFileFormat(fileName, contentType);
        
        // Generate unique key for the file
        String fileKey = UUID.randomUUID() + "_" + fileName;
        
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileKey)
                    .contentType(contentType)
                    .build();
            
            s3Client.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(fileData)
            );
            
            return generateS3Url(fileKey);
            
        } catch (AuthException e) {
            // Re-throw validation exceptions
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file to S3: " + e.getMessage());
        }
    }
    
    /**
     * Validate file format
     * @param fileName - original filename
     * @param contentType - MIME type of the file
     */
    private void validateFileFormat(String fileName, String contentType) {
        if (fileName == null || fileName.isEmpty()) {
            throw new AuthException("File name cannot be empty");
        }
        
        // Check file extension
        String fileExtension = getFileExtension(fileName).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new AuthException("Invalid file format. Only .png, .jpg, and .jpeg are allowed");
        }
        
        // Check MIME type
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new AuthException("Invalid file type. Only image files are allowed");
        }
    }
    
    /**
     * Get file extension from filename
     * @param fileName - original filename
     * @return file extension with dot (e.g., ".jpg")
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(lastDotIndex);
    }
    
    /**
     * Generate S3 object URL
     * @param fileKey - S3 object key
     * @return S3 object URL
     */
    public String generateS3Url(String fileKey) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s", 
                bucketName, region, fileKey);
    }
}
