# Notification Service

A robust, multi-channel notification service built with Spring Boot that enables sending notifications across multiple platforms including Email, SMS, and WhatsApp.

## 🎯 Overview

The Notification Service is a microservice designed to handle notification delivery across multiple channels. It provides a unified API for sending notifications through different providers while managing notification logs, templates, and configurations with intelligent provider selection and fallback mechanisms.

**Project Group ID:** `com.axd`
**Version:** `0.0.1-SNAPSHOT`
**Java Version:** 21
**Spring Boot Version:** 3.5.7
**API Base Path:** `/notification-service`

## ✨ Features

- **Multi-Channel Support:**
  - Email (AWS SES, Postmark)
  - SMS (Twilio, AWS SNS)
  - WhatsApp (Twilio)

- **Key Capabilities:**
  - Template-based notification system with dynamic content rendering
  - Request deduplication using `requestId` to prevent duplicate sends
  - Notification history and detailed logging
  - Provider selection with configurable rankings and fallback mechanisms
  - Configuration management with Redis caching for high performance
  - User notification tracking and status retrieval
  - Support for both template-based and direct content notifications

- **Technology Stack:**
  - Spring Boot 3.5.7
  - Spring Data JPA with Hibernate
  - MySQL Database
  - AWS SDK (SES, SNS)
  - Twilio SDK
  - Lombok for Code Generation
  - SpringDoc OpenAPI/Swagger Documentation

## 🛠️ Prerequisites

Before you begin, ensure you have the following installed:

- Java 21
- Maven 3.6+
- MySQL 8.0+

## 📦 Installation & Setup

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/notification-service.git
cd notification-service
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/notification-service`

## 📋 API Endpoints

All endpoints are prefixed with `/notification-service/notifications`

### 1. Send Notification

**Endpoint:** `POST /notifications/send`

**Request Body:**
```json
{
  "requestId": "unique-request-id-12345",
  "userId": 1,
  "type": "TEMPLATE_BASED",
  "channel": "EMAIL",
  "data": {
    "templateId": "welcome_email",
    "userName": "John Doe",
    "activationLink": "https://example.com/activate",
    "recipientEmail": "john@example.com"
  }
}
```

**Alternative - Direct Content:**
```json
{
  "requestId": "unique-request-id-67890",
  "userId": 2,
  "type": "DIRECT_CONTENT",
  "channel": "SMS",
  "data": {
    "phoneNumber": "+1234567890",
    "message": "Your verification code is: 123456"
  }
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "notificationId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "SUCCESS"
  }
}
```

### 2. Get User Notifications

**Endpoint:** `GET /notifications/user/{userId}`

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": 1,
    "notifications": [
      {
        "id": 1,
        "notificationId": "550e8400-e29b-41d4-a716-446655440000",
        "channel": "EMAIL",
        "provider": "AWS_SES",
        "state": "SUCCESS",
        "createdAt": "2026-03-03T10:30:00"
      }
    ]
  }
}
```

### 3. Get Notification Status

**Endpoint:** `GET /notifications/status/{notificationId}`

**Response:**
```json
{
  "success": true,
  "data": {
    "status": "SUCCESS"
  }
}
```

## 🔌 Supported Notification Channels & Providers

### Email Channel
- **AWS SES** - High deliverability, enterprise-grade
- **Postmark** - Developer-friendly email service

### SMS Channel
- **Twilio SMS** - Global SMS delivery
- **AWS SNS** - AWS-integrated SMS service

### WhatsApp Channel
- **Twilio WhatsApp** - Rich media messaging, interactive templates

## 🏗️ Project Structure

```
notification-service/
├── src/
│   ├── main/
│   │   ├── java/com/axd/notificationService/
│   │   │   ├── channel/
│   │   │   │   ├── implementations/
│   │   │   │   │   ├── EmailChannelService.java
│   │   │   │   │   ├── SmsChannelService.java
│   │   │   │   │   └── WhatsAppChannelService.java
│   │   │   │   └── NotificationChannelService.java
│   │   │   ├── config/
│   │   │   │   ├── AwsSesConfig.java
│   │   │   │   ├── AwsSnsConfig.java
│   │   │   │   ├── TwilioConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── KafkaConfig.java
│   │   │   │   └── WebClientConfig.java
│   │   │   ├── controller/
│   │   │   │   └── NotificationController.java
│   │   │   ├── enums/
│   │   │   │   ├── NotificationChannel.java     # EMAIL, SMS, WHATSAPP
│   │   │   │   ├── NotificationProvider.java    # AWS_SES, TWILIO_SMS, etc.
│   │   │   │   ├── NotificationType.java        # TEMPLATE_BASED, DIRECT_CONTENT
│   │   │   │   ├── NotificationRequestStatus.java
│   │   │   │   └── LogContextKey.java
│   │   │   ├── exceptions/
│   │   │   │   ├── AbstractException.java
│   │   │   │   ├── ValidationException.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   └── handler/GlobalExceptionsHandler.java
│   │   │   ├── factory/
│   │   │   │   ├── ChannelFactory.java
│   │   │   │   └── ProviderFactory.java
│   │   │   ├── model/
│   │   │   │   ├── entity/
│   │   │   │   │   ├── BaseEntity.java
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── NotificationLog.java
│   │   │   │   │   ├── NotificationTemplate.java
│   │   │   │   │   └── ConfigEntity.java
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/NotificationRequest.java
│   │   │   │   │   ├── response/
│   │   │   │   │   │   ├── GenericResponse.java
│   │   │   │   │   │   ├── NotificationResponse.java
│   │   │   │   │   │   ├── NotificationStatus.java
│   │   │   │   │   │   └── UserNotification.java
│   │   │   │   │   ├── EmailDto.java
│   │   │   │   │   ├── SmsDto.java
│   │   │   │   │   └── WhatsappDto.java
│   │   │   │   └── ProviderConfig.java
│   │   │   ├── provider/
│   │   │   │   ├── NotificationProviderService.java
│   │   │   │   ├── emailProvider/AwsEmailProviderService.java
│   │   │   │   ├── smsProvider/
│   │   │   │   │   ├── AwsSmsProviderService.java
│   │   │   │   │   └── TwilioSmsProviderService.java
│   │   │   │   └── whatsAppProvider/TwilioWhatsAppProviderService.java
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── NotificationLogRepository.java
│   │   │   │   ├── NotificationTemplateRepository.java
│   │   │   │   └── ConfigRepository.java
│   │   │   ├── service/
│   │   │   │   ├── NotificationService.java
│   │   │   │   ├── NotificationLogService.java
│   │   │   │   ├── ConfigCacheService.java
│   │   │   │   └── ProviderSelectionService.java
│   │   │   ├── util/
│   │   │   │   ├── TemplateRenderer.java
│   │   │   │   └── CommonUtils.java
│   │   │   ├── constants/
│   │   │   │   ├── AppConstants.java
│   │   │   │   └── ControllerConstants.java
│   │   │   └── NotificationServiceApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/com/axd/notificationService/
├── pom.xml
├── README.md
└── HELP.md
```

## 🔑 Key Components

### NotificationService
- Core service orchestrating notification delivery
- Handles request deduplication using `requestId`
- Routes requests to appropriate channels via `ChannelFactory`
- Manages notification status and user notifications retrieval

### NotificationChannelService Interface
- Abstract contract for all channel implementations
- Implemented by `EmailChannelService`, `SmsChannelService`, `WhatsAppChannelService`
- Each implementation selects appropriate providers using `ProviderSelectionService`

### ProviderFactory & ChannelFactory
- **ChannelFactory:** Maps `NotificationChannel` enum to channel service implementations
- **ProviderFactory:** Dynamically selects providers based on configuration and availability

### ProviderSelectionService
- Selects providers for a channel based on cached configuration
- Filters enabled providers and sorts by rank
- Implements fallback mechanism - if primary provider fails, tries next in rank

### ConfigCacheService
- Manages configuration caching with Redis
- Eliminates repeated database queries for provider configurations
- Supports cache invalidation and refresh

### NotificationLogService
- Logs all notification attempts and their outcomes
- Tracks status, provider used, and delivery results
- Enables notification history and debugging

## 🗄️ Database Schema

### Core Entities

#### User
```java
@Entity
public class User {
    private Long id;
    private String name;
}
```

#### NotificationLog
```java
@Entity
public class NotificationLog extends BaseEntity {
    private Long id;
    private String requestId;           // Used for deduplication
    private String notificationId;
    private Long userId;
    private String eventId;
    private String type;                // TEMPLATE_BASED, DIRECT_CONTENT
    private String channel;             // EMAIL, SMS, WHATSAPP
    private String provider;            // AWS_SES, TWILIO_SMS, etc.
    private String providerNotificationId;
    private String state;               // SUCCESS, FAILURE, PENDING
}
```

#### NotificationTemplate
```java
@Entity
public class NotificationTemplate extends BaseEntity {
    private Long id;
    private String type;               // Template type (e.g., ORDER_PLACED)
    private String channel;            // EMAIL, SMS
    private String subject;            // For email templates
    private String bodyTemplate;       // Template with placeholders
}
```

#### ConfigEntity (with backtick escaping for reserved keywords)
```java
@Entity
public class ConfigEntity extends BaseEntity {
    private Long id;
    @Column(name = "`group`")
    private String group;              // Configuration group (EMAIL, SMS, etc.)
    @Column(name = "`key`")
    private String key;                // Configuration key
    private String value;              // Configuration value
}
```

#### BaseEntity
```java
@MappedSuperclass
public class BaseEntity {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

## 🔐 Configuration Management

Configuration is stored in the database and cached in Redis for performance:

```java
// Example: Providers for EMAIL channel stored as JSON
{
  "group": "CHANNEL_PROVIDERS",
  "key": "EMAIL",
  "value": {
    "providers": [
      {
        "name": "AWS_SES",
        "enabled": true,
        "rank": 1
      },
      {
        "name": "POSTMARK",
        "enabled": true,
        "rank": 2
      }
    ]
  }
}
```

## 📊 Notification Processing Flow

```
1. Receive Notification Request (POST /notifications/send)
   ↓
2. Validate Request & Check Required Fields
   ↓
3. Check Deduplication (Query by requestId)
   └─→ If exists, return existing notification ID
   ↓
4. Determine Channel from Request
   ↓
5. Get Channel Implementation via ChannelFactory
   ↓
6. Channel Service:
   ├─ Get Providers via ProviderSelectionService
   ├─ Try providers in rank order
   ├─ If provider succeeds → return result
   └─ If provider fails → try next provider (fallback)
   ↓
7. Log Notification (if not failure)
   ↓
8. Return Response with notificationId & status
```

## 🔄 Notification Types

### 1. TEMPLATE_BASED
- Uses pre-configured templates with placeholders
- Requires `templateId` in request data
- Template variables substituted at runtime
- Example: Welcome email with dynamic user name

### 2. DIRECT_CONTENT
- Sends content directly without templates
- Message/body provided in request
- No template lookup required
- Example: OTP or verification code

## 📝 Logging

The application uses SLF4J with Logback for comprehensive logging:

- **Request-level logging:** Track requests through the system
- **Provider-level logging:** Monitor each provider's performance
- **Error logging:** Capture and log exceptions with stack traces
- **Contextual information:** Log through `LogContextKey` enumeration

## 🧪 Testing

Run the test suite:

```bash
mvn test
```

Test class: `NotificationServiceApplicationTests.java`

## 🚀 Deployment

### Docker Build

Create a `Dockerfile` in the project root:

```dockerfile
FROM openjdk:21-slim
WORKDIR /app
COPY target/notification-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
```

Build and run:

```bash
docker build -t notification-service:latest .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/notification_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=root \
  notification-service:latest
```

### Docker Compose

```yaml
version: '3.8'
services:
  notification-service:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/notification_db
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      SPRING_DATA_REDIS_HOST: redis
      SPRING_DATA_REDIS_PORT: 6379
      AWS_SES_ACCESS_KEY: ${AWS_ACCESS_KEY}
      AWS_SES_SECRET_KEY: ${AWS_SECRET_KEY}
      TWILIO_ACCOUNT_SID: ${TWILIO_ACCOUNT_SID}
      TWILIO_AUTH_TOKEN: ${TWILIO_AUTH_TOKEN}
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: notification_db
    volumes:
      - mysql_data:/var/lib/mysql

  redis:
    image: redis:7.0-alpine
    ports:
      - "6379:6379"

volumes:
  mysql_data:
```

## 📚 API Documentation

Interactive API documentation is available via Swagger UI:

```
http://localhost:8080/notification-service/swagger-ui.html
```

OpenAPI specification:
```
http://localhost:8080/notification-service/v3/api-docs
```