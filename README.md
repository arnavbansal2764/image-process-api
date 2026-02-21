# Image Processing API

A Spring Boot REST API for user authentication, image upload, storage, and transformation with MongoDB persistence and AWS S3 file storage.

**Project URL:** https://roadmap.sh/projects/image-processing-service
## Features

✅ **User Authentication** - JWT-based secure registration and login  
✅ **Image Upload** - File upload with format validation (.png, .jpg, .jpeg)  
✅ **AWS S3 Integration** - Cloud storage with presigned URLs  
✅ **MongoDB Storage** - Metadata persistence for users and images  
✅ **Image Transformations** - Resize, crop, rotate, filters (grayscale, sepia), format conversion  
✅ **Pagination** - Paginated image listing with configurable page size  
✅ **Global Error Handling** - Consistent error responses with timestamps  
✅ **Security** - BCrypt password hashing with JWT token validation  

---

## Prerequisites

Before running the project, ensure you have the following installed:

- **Java 21** or higher
- **Maven 3.8+**
- **MongoDB** (cloud or local)
- **AWS S3 Bucket** with access credentials
- **ImageMagick** (for image transformations)

### Installation

#### macOS
```bash
brew install java21 maven imagemagick mongodb-community
```

#### Linux (Ubuntu/Debian)
```bash
# Java & Maven
sudo apt update
sudo apt install openjdk-21-jdk maven

# ImageMagick
sudo apt install imagemagick

# MongoDB (optional, if using locally)
sudo apt install mongodb
```

#### Windows
- Download and install [Java 21 JDK](https://www.oracle.com/java/technologies/downloads/)
- Download and install [Maven](https://maven.apache.org/download.cgi)
- Download and install [ImageMagick](https://imagemagick.org/script/download.php)
- Download and install [MongoDB Community](https://www.mongodb.com/try/download/community)

---

## Configuration

### Environment Variables

Create a `.env` file or export these variables before running the application:

```bash
# MongoDB
export MONGODB_URI="mongodb+srv://username:password@cluster.mongodb.net/image-process-api?retryWrites=true&w=majority"

# AWS S3
export AWS_ACCESS_KEY="your-aws-access-key"
export AWS_SECRET_KEY="your-aws-secret-key"
export AWS_REGION="ap-south-1"  # or your preferred region
export AWS_S3_BUCKET_NAME="your-s3-bucket-name"
```

### application.yaml Configuration

The `src/main/resources/application.yaml` contains:

```yaml
spring:
    application:
        name: image-process-api
    mongodb:
        uri: ${MONGODB_URI}
        database: image-process-api
    servlet:
        multipart:
            max-file-size: 50MB
            max-request-size: 50MB

jwt:
    secret: mySecretKeyForJWTTokenGenerationPleaseChangeInProduction123456789
    expiration: 86400000  # 24 hours in milliseconds

aws:
    s3:
        access-key: ${AWS_ACCESS_KEY}
        secret-key: ${AWS_SECRET_KEY}
        region: ${AWS_REGION:ap-south-1}
        bucket-name: ${AWS_S3_BUCKET_NAME}
```

---

## Build & Run

### Build the Project
```bash
cd image-process-api
mvn clean package
```

### Run the Application

**With Environment Variables:**
```bash
export MONGODB_URI="mongodb+srv://admin:password@cluster0.mongodb.net/image-process-api"
export AWS_ACCESS_KEY="AKIAW3MEECLSS2P2XI3M"
export AWS_SECRET_KEY="secret-key"
export AWS_REGION="ap-south-1"
export AWS_S3_BUCKET_NAME="image-process-api"

java -jar target/image-process-api-0.0.1-SNAPSHOT.jar
```

**Or inline:**
```bash
java -jar target/image-process-api-0.0.1-SNAPSHOT.jar \
  --mongodb.uri="mongodb+srv://admin:password@cluster0.mongodb.net/image-process-api" \
  --aws.s3.access-key="AKIAW3MEECLSS2P2XI3M" \
  --aws.s3.secret-key="secret-key" \
  --aws.s3.region="ap-south-1" \
  --aws.s3.bucket-name="image-process-api"
```

The API will start on **http://localhost:8080**

---

## API Endpoints

### 1. Health Check

**GET** `/`
```bash
curl http://localhost:8080/
```
**Response:**
```
Welcome to Image Processing API
```

---

### 2. User Registration

**POST** `/register`

Register a new user with unique username and password.

**Request:**
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass@123"
  }'
```

**Response (201 Created):**
```json
{
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "username": "john_doe",
    "password": "$2a$10$hashedpassword"
  },
  "jwt": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1MDdmMWY3N2JjZjg2Y2Q3OTk0MzkwMTEiLCJ1c2VybmFtZSI6ImpvaG5fZG9lIiwiaWF0IjoxNjA0ODk4OTEwLCJleHAiOjE2MDQ5ODUzMTB9.signature"
}
```

**Error Response (400):**
```json
{
  "message": "Username already exists",
  "status": 400,
  "timestamp": 1771659380126
}
```

---

### 3. User Login

**POST** `/login`

Authenticate user and receive JWT token.

**Request:**
```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "password": "SecurePass@123"
  }'
```

**Response (200 OK):**
```json
{
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "username": "john_doe",
    "password": "$2a$10$hashedpassword"
  },
  "jwt": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI1MDdmMWY3N2JjZjg2Y2Q3OTk0MzkwMTEiLCJ1c2VybmFtZSI6ImpvaG5fZG9lIiwiaWF0IjoxNjA0ODk4OTEwLCJleXAiOjE2MDQ5ODUzMTB9.signature"
}
```

**Error Response (401):**
```json
{
  "message": "Invalid credentials",
  "status": 401,
  "timestamp": 1771659380126
}
```

---

### 4. Upload Image

**POST** `/upload`

Upload an image file to AWS S3 with MongoDB metadata storage. Requires valid JWT token.

**Request:**
```bash
JWT_TOKEN="your-jwt-token-from-login"

curl -X POST http://localhost:8080/upload \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -F "file=@/path/to/image.jpg"
```

**Response (200 OK):**
```json
{
  "fileUrl": "https://image-process-api.s3.ap-south-1.amazonaws.com/john_doe/image_1771659400.jpg?X-Amz-Algorithm=...",
  "fileName": "image_1771659400.jpg",
  "message": "File uploaded successfully"
}
```

**Error Response (400):**
```json
{
  "message": "Invalid file format. Allowed: .png, .jpg, .jpeg",
  "status": 400,
  "timestamp": 1771659380126
}
```

---

### 5. Get Single Image

**GET** `/images/{id}`

Retrieve a single image by its MongoDB ID.

**Request:**
```bash
curl -X GET http://localhost:8080/images/507f1f77bcf86cd799439012 \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439012",
  "fileUrl": "https://image-process-api.s3.ap-south-1.amazonaws.com/john_doe/image_1771659400.jpg",
  "fileName": "image_1771659400.jpg",
  "contentType": "image/jpeg",
  "uploadedAt": "2025-12-21T10:30:00Z",
  "uploadedBy": "john_doe"
}
```

---

### 6. List Images (Paginated)

**GET** `/images?page=0&limit=10`

Retrieve paginated list of all uploaded images.

**Request:**
```bash
curl -X GET "http://localhost:8080/images?page=0&limit=10" \
  -H "Authorization: Bearer $JWT_TOKEN"
```

**Response (200 OK):**
```json
{
  "data": [
    {
      "id": "507f1f77bcf86cd799439012",
      "fileUrl": "https://image-process-api.s3.ap-south-1.amazonaws.com/john_doe/image_1771659400.jpg",
      "fileName": "image_1771659400.jpg",
      "contentType": "image/jpeg",
      "uploadedAt": "2025-12-21T10:30:00Z",
      "uploadedBy": "john_doe"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10
}
```

---

### 7. Transform Image

**POST** `/images/{id}/transform`

Apply transformations to an uploaded image and save the result to S3.

**Supported Transformations:**
- **resize**: `{"width": 500, "height": 500}` - Resize to width x height (forces aspect ratio)
- **crop**: `{"x": 50, "y": 50, "width": 400, "height": 400}` - Crop with offset
- **rotate**: `{"degrees": 90}` - Rotate by degrees (90, 180, 270, -90, etc.)
- **format**: `{"newFormat": "png"}` - Convert to another format (.png, .jpg, .jpeg)
- **filters**: `{"grayscale": true, "sepia": false}` - Apply filters

**Request:**
```bash
JWT_TOKEN="your-jwt-token"
IMAGE_ID="507f1f77bcf86cd799439012"

curl -X POST http://localhost:8080/images/$IMAGE_ID/transform \
  -H "Authorization: Bearer $JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "resize": {"width": 500, "height": 500},
    "crop": {"x": 0, "y": 0, "width": 400, "height": 400},
    "rotate": {"degrees": 90},
    "format": {"newFormat": "png"},
    "filters": {"grayscale": false, "sepia": true}
  }'
```

**Response (200 OK):**
```json
{
  "id": "507f1f77bcf86cd799439013",
  "fileUrl": "https://image-process-api.s3.ap-south-1.amazonaws.com/john_doe/image_1771659450_transformed.png",
  "fileName": "image_1771659450_transformed.png",
  "contentType": "image/png",
  "uploadedAt": "2025-12-21T10:35:00Z",
  "uploadedBy": "john_doe"
}
```

---

## Example Workflow

### Complete Flow: Register → Login → Upload → Transform

```bash
#!/bin/bash

# 1. Register User
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test@123"}')

JWT=$(echo $REGISTER_RESPONSE | jq -r '.jwt')
echo "JWT Token: $JWT"

# 2. Upload Image
UPLOAD_RESPONSE=$(curl -s -X POST http://localhost:8080/upload \
  -H "Authorization: Bearer $JWT" \
  -F "file=@/path/to/image.jpg")

IMAGE_ID=$(echo $UPLOAD_RESPONSE | jq -r '.id')
FILE_URL=$(echo $UPLOAD_RESPONSE | jq -r '.fileUrl')
echo "Image ID: $IMAGE_ID"
echo "File URL: $FILE_URL"

# 3. Transform Image
TRANSFORM_RESPONSE=$(curl -s -X POST http://localhost:8080/images/$IMAGE_ID/transform \
  -H "Authorization: Bearer $JWT" \
  -H "Content-Type: application/json" \
  -d '{"resize": {"width": 500, "height": 500}}')

echo "Transformed Image:"
echo $TRANSFORM_RESPONSE | jq '.'

# 4. List All Images
curl -s -X GET "http://localhost:8080/images?page=0&limit=10" \
  -H "Authorization: Bearer $JWT" | jq '.'
```

---

## Project Structure

```
image-process-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/image_process_api/
│   │   │   ├── ImageProcessApiApplication.java      # Main Spring Boot app
│   │   │   ├── controller/
│   │   │   │   └── main.java                        # REST endpoints
│   │   │   ├── service/
│   │   │   │   ├── AuthService.java                 # User registration & login
│   │   │   │   ├── S3Service.java                   # AWS S3 operations
│   │   │   │   ├── ImageService.java                # Image metadata & upload logic
│   │   │   │   └── ImageTransformationService.java  # ImageMagick transformations
│   │   │   ├── security/
│   │   │   │   ├── JwtTokenProvider.java            # JWT generation & validation
│   │   │   │   └── SecurityConfig.java              # Spring Security config
│   │   │   ├── entity/
│   │   │   │   ├── User.java                        # User MongoDB document
│   │   │   │   └── Image.java                       # Image metadata document
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java              # User queries
│   │   │   │   └── ImageRepository.java             # Image queries
│   │   │   ├── dto/
│   │   │   │   ├── AuthResponse.java                # Auth response DTO
│   │   │   │   ├── RegisterRequest.java             # Registration request
│   │   │   │   ├── LoginRequest.java                # Login request
│   │   │   │   ├── FileUploadResponse.java          # Upload response
│   │   │   │   ├── PaginatedResponse.java           # Pagination wrapper
│   │   │   │   ├── TransformationRequest.java       # Transform request
│   │   │   │   ├── ResizeRequest.java               # Resize options
│   │   │   │   ├── CropRequest.java                 # Crop options
│   │   │   │   └── FiltersRequest.java              # Filter options
│   │   │   ├── exception/
│   │   │   │   ├── AuthException.java               # Custom auth exception
│   │   │   │   ├── ErrorResponse.java               # Error response DTO
│   │   │   │   └── GlobalExceptionHandler.java      # Global exception handling
│   │   │   └── config/
│   │   │       └── S3Config.java                    # AWS S3 configuration
│   │   └── resources/
│   │       └── application.yaml                     # Configuration file
│   └── test/
│       └── java/com/example/image_process_api/
│           └── ImageProcessApiApplicationTests.java # Integration tests
├── pom.xml                                           # Maven dependencies
└── README.md                                         # This file
```

---

## Technology Stack

| Component | Version | Purpose |
|-----------|---------|---------|
| Spring Boot | 4.0.2 | Web framework & dependency injection |
| Java | 21 | Programming language |
| MongoDB | Latest | NoSQL database for user & image metadata |
| AWS SDK | 2.24.0 | S3 bucket operations & cloud storage |
| JJWT | 0.12.3 | JWT token generation & validation |
| Spring Security | 4.0.2 | Authentication & authorization |
| Spring Data MongoDB | 4.0.2 | MongoDB ORM & repository pattern |
| ImageMagick | 7.x+ | Image processing & transformations |
| Lombok | Latest | Annotation processing for boilerplate |

---

## Troubleshooting

### ImageMagick Not Found
```
Error: convert: command not found
```
**Solution:** Install ImageMagick on your system:
```bash
# macOS
brew install imagemagick

# Linux
sudo apt install imagemagick

# Windows - Download from https://imagemagick.org/script/download.php
```

### MongoDB Connection Failed
```
Error: MongoSocketOpenException: Exception opening socket
```
**Ensure MongoDB URI is correct:**
```bash
# Test connection
mongosh "mongodb+srv://username:password@cluster.mongodb.net/image-process-api"
```

### AWS S3 Access Denied
```
Error: User: arn:aws:iam::xxx is not authorized
```
**Check:**
1. AWS credentials are correct
2. S3 bucket exists in specified region
3. IAM user has S3:PutObject, S3:GetObject permissions

### JWT Token Expired
```json
{
  "message": "JWT token has expired",
  "status": 401
}
```
**Solution:** Login again to get a fresh token (expires after 24 hours)

---

## API Security Notes

- All endpoints except `/register` and `/login` require JWT token in `Authorization: Bearer <token>` header
- Passwords are hashed with BCrypt (10 rounds)
- JWT tokens expire after 24 hours
- File uploads limited to 50MB
- Only image formats allowed: `.png`, `.jpg`, `.jpeg`

---

## Future Enhancements

- [ ] JWT token refresh mechanism
- [ ] Delete image endpoint
- [ ] Batch image upload
- [ ] User profile management endpoint
- [ ] Advanced filters (blur, sharpen, edge detection)
- [ ] Image sharing & public links
- [ ] Swagger/OpenAPI documentation
- [ ] Rate limiting per user
- [ ] Image compression & optimization
- [ ] WebSocket support for real-time transformations

---

## License

This project is open source and available under the MIT License.

---

## Support

For issues or questions, please create an issue in the repository or contact the development team.

---

**Last Updated:** February 21, 2026  
**API Version:** 1.0.0
