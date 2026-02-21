package com.example.image_process_api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.image_process_api.dto.TransformationRequest;
import com.example.image_process_api.entity.Image;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageTransformationService {
    
    @Autowired
    private ImageService imageService;
    
    @Autowired
    private S3Service s3Service;
    
    private static final String TEMP_DIR = "/tmp/image-process/";
    
    /**
     * Apply transformations to an image
     * @param imageId - ID of the image to transform
     * @param transformationRequest - transformations to apply
     * @return Transformed image details
     */
    public Image applyTransformations(String imageId, TransformationRequest transformationRequest) {
        // Get the original image
        Image originalImage = imageService.getImageById(imageId);
        
        try {
            // Create temp directory if not exists
            Files.createDirectories(Paths.get(TEMP_DIR));
            
            // Download image from S3
            byte[] imageBytes = downloadImageFromUrl(originalImage.getFileUrl());
            
            // Create temp input file
            String inputFilePath = TEMP_DIR + "input_" + System.currentTimeMillis() + ".jpg";
            Files.write(Paths.get(inputFilePath), imageBytes);
            
            // Apply transformations
            String outputFormat = transformationRequest.getFormat() != null ? 
                    transformationRequest.getFormat() : "jpg";
            String outputFilePath = TEMP_DIR + "output_" + System.currentTimeMillis() + "." + outputFormat;
            applyImageMagickTransformations(inputFilePath, outputFilePath, transformationRequest);
            
            // Read transformed image
            byte[] transformedBytes = Files.readAllBytes(Paths.get(outputFilePath));
            
            // Determine output format
            String outputFileName = "transformed_" + System.currentTimeMillis() + "." + outputFormat;
            String mimeType = "image/" + outputFormat;
            
            // Upload transformed image to S3
            s3Service.uploadFile(transformedBytes, outputFileName, mimeType);
            
            // Clean up temp files
            new File(inputFilePath).delete();
            new File(outputFilePath).delete();
            
            // Save transformed image metadata to MongoDB
            String uploadedImageUrl = imageService.uploadImage(transformedBytes, outputFileName, mimeType, null);
            
            return imageService.getImageById(imageService.getLastUploadedImageId());
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to transform image: " + e.getMessage());
        }
    }
    
    /**
     * Apply ImageMagick transformations using ProcessBuilder
     */
    private void applyImageMagickTransformations(String inputPath, String outputPath, TransformationRequest request) throws Exception {
        List<String> command = new ArrayList<>();
        command.add("convert");
        command.add(inputPath);
        
        // Apply resize
        if (request.getResize() != null) {
            Integer width = request.getResize().getWidth();
            Integer height = request.getResize().getHeight();
            if (width != null && height != null) {
                command.add("-resize");
                command.add(width + "x" + height + "!");
            }
        }
        
        // Apply crop
        if (request.getCrop() != null) {
            Integer x = request.getCrop().getX();
            Integer y = request.getCrop().getY();
            Integer width = request.getCrop().getWidth();
            Integer height = request.getCrop().getHeight();
            if (x != null && y != null && width != null && height != null) {
                command.add("-crop");
                command.add(width + "x" + height + "+" + x + "+" + y);
                command.add("+repage");
            }
        }
        
        // Apply rotate
        if (request.getRotate() != null) {
            command.add("-rotate");
            command.add(request.getRotate().toString());
        }
        
        // Apply filters
        if (request.getFilters() != null) {
            if (request.getFilters().getGrayscale() != null && request.getFilters().getGrayscale()) {
                command.add("-colorspace");
                command.add("Gray");
            }
            if (request.getFilters().getSepia() != null && request.getFilters().getSepia()) {
                command.add("-sepia-tone");
                command.add("80%");
            }
        }
        
        command.add(outputPath);
        
        // Execute ImageMagick command
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        int exitCode = process.waitFor();
        
        if (exitCode != 0) {
            throw new RuntimeException("ImageMagick conversion failed with exit code: " + exitCode);
        }
    }
    
    /**
     * Download image from URL
     */
    private byte[] downloadImageFromUrl(String fileUrl) throws Exception {
        java.net.URL url = new java.net.URL(fileUrl);
        java.net.URLConnection connection = url.openConnection();
        java.io.InputStream input = connection.getInputStream();
        byte[] buffer = new byte[8192];
        int bytesRead;
        java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream();
        
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        
        input.close();
        return output.toByteArray();
    }
}
