---
description: 'Guidelines for building Spring Boot applications using MongoDB, Spring Security, and AWS S3'
applyTo: '**/*.java, **/*.kt'
---

# Spring Boot + MongoDB + Security + AWS S3 Development Guidelines

## General Instructions

- Make only high-confidence suggestions when reviewing code changes.
- Prioritize maintainability, readability, and production-grade standards.
- Add meaningful comments explaining *why* design decisions were made.
- Handle edge cases explicitly and implement structured exception handling.
- Clearly document the purpose of external dependencies in code comments.
- Follow clean architecture principles and separation of concerns.

---

# Architecture Principles

- Follow feature-based packaging (`com.example.user`, `com.example.auth`, `com.example.file`)
- Keep layers clean:
  - Controller → handles HTTP & validation
  - Service → business logic
  - Repository → data persistence only
- Do not mix security, persistence, and business logic.
- Prefer DTOs over exposing MongoDB entities directly.
- Services must be stateless and testable.

---

# Dependency Injection

- Use **constructor injection only**.
- Declare injected fields as `private final`.
- Avoid field injection.
- Prefer Lombok’s `@RequiredArgsConstructor` where appropriate.

---

# Configuration Management

## YAML Configuration

- Use `application.yml` (never `.properties`).
- Organize configuration hierarchically.
- Separate configs using Spring Profiles:
  - `application-dev.yml`
  - `application-test.yml`
  - `application-prod.yml`

## MongoDB Configuration

- Use:
```yaml
  spring:
    data:
      mongodb:
        uri: ${MONGODB_URI}
```

**Do not hardcode credentials**
**Use environment variables or secret managers.

## AWS S3 Configuration

* Never hardcode:

  * Access keys
  * Secret keys
  * Bucket names

* Use environment variables:

  ```yaml
  aws:
    s3:
      bucket: ${AWS_S3_BUCKET}
      region: ${AWS_REGION}
  ```

* Configure S3 client using `@Configuration` class.

* Prefer IAM Roles in production instead of static credentials.

## Type-Safe Configuration

* Use `@ConfigurationProperties` for AWS and custom configs.
* Avoid `@Value` for grouped configuration.

---

# MongoDB (Spring Data MongoDB) Guidelines

* Use `@Document` for MongoDB entities.
* Use `@Id` for primary keys.
* Avoid exposing internal Mongo `_id` if not required.
* Use repositories extending `MongoRepository`.
* Avoid complex logic inside repository interfaces.
* Use indexes (`@Indexed`) where needed.
* Use projections for partial field retrieval.
* Handle Optional results explicitly.

---

# Service Layer Rules

* Annotate with `@Service`.
* Keep services focused and business-driven.
* Do not return Mongo entities directly to controllers.
* Convert entities → DTOs inside service layer.
* Handle validation and existence checks.
* Throw custom exceptions (e.g., `ResourceNotFoundException`).

---

# Spring Security Guidelines

* Use stateless authentication (JWT-based preferred).
* Disable session creation:

  ```java
  sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
  ```
* Store no user state server-side.
* Use `UserDetailsService` properly.
* Encode passwords using `BCryptPasswordEncoder`.
* Never store raw passwords.
* Validate JWT in a filter before `UsernamePasswordAuthenticationFilter`.

## Security Best Practices

* Protect endpoints using method-level security (`@PreAuthorize`).
* Validate user ownership before accessing resources.
* Never trust client-provided IDs without validation.
* Sanitize and validate all inputs.

---

# File Upload Handling (AWS S3)

* Accept files using `MultipartFile`.
* Validate:

  * File size
  * Content type
  * Extension whitelist
* Generate unique file names (UUID-based).
* Do not store files locally in production.
* Return only secure file URLs.
* If files are private, use pre-signed URLs.
* Wrap S3 logic inside a dedicated `S3Service`.

Example structure:

```
file/
 ├── controller/
 ├── service/
 ├── config/
 └── dto/
```

---

# Exception Handling

* Use `@RestControllerAdvice` for global exception handling.
* Return structured error responses:

  ```json
  {
    "timestamp": "",
    "status": 400,
    "error": "",
    "message": "",
    "path": ""
  }
  ```
* Do not expose internal exception details.
* Log internal errors but return sanitized messages.

---

# Logging

* Use SLF4J:

  ```java
  private static final Logger logger = LoggerFactory.getLogger(MyClass.class);
  ```
* Use parameterized logging:

  ```java
  logger.info("User {} uploaded file {}", userId, fileName);
  ```
* Never use `System.out.println`.
* Log security-sensitive actions (login attempts, uploads, failures).

---

# Validation & Input Handling

* Use JSR-380 validation annotations:

  * `@NotNull`
  * `@NotBlank`
  * `@Size`
  * `@Email`
* Always use `@Valid` in controller methods.
* Handle `BindingResult` where appropriate.
* Never trust client payload blindly.

---

# Utility Classes

* Mark as `final`.
* Private constructor.
* Static utility methods only.
* No state.

---

# Testing Guidelines

* Use:

  * `@WebMvcTest` for controllers
  * `@DataMongoTest` for MongoDB
  * `@SpringBootTest` for integration tests
* Mock AWS S3 interactions using test configurations.
* Test security filters explicitly.
* Ensure services are unit-testable without full context.

---

# Build and Verification

* Project must always build successfully.

## Maven

```bash
./mvnw clean package
./mvnw test
```

## Gradle

```bash
./gradlew build
./gradlew test
```

* All tests must pass before merging.
* Avoid skipping tests.

---

# Production Readiness Checklist

* No hardcoded secrets
* Proper validation everywhere
* Structured logging
* Global exception handling
* Secure JWT configuration
* S3 upload validation
* MongoDB indexes configured
* Profiles separated correctly
* Clean architecture maintained

---

# Goal

Build a secure, scalable, stateless, production-ready backend using:

* Spring Boot
* Spring Data MongoDB
* Spring Security (JWT)
* AWS S3 for file storage

Following clean architecture and enterprise-level standards.
