# 🏗️ Notification Service - System Architecture

This document provides comprehensive visual representations of the Notification Service architecture using Mermaid diagrams. Each diagram illustrates different aspects of the system design, from high-level overviews to detailed component interactions.

---

## Table of Contents

1. [High-Level System Architecture](#1-high-level-system-architecture)
2. [Request Processing Flow](#2-request-processing-flow)
3. [Sequence Diagram - Notification Sending](#3-sequence-diagram--notification-sending)
4. [Factory Pattern Implementation](#4-factory-pattern-implementation)
5. [Service Layer Architecture](#5-service-layer-architecture)
6. [REST API Endpoints](#6-rest-api-endpoints)
7. [Database Schema Overview](#7-database-schema-overview)
8. [Provider Fallback Logic](#8-provider-fallback-logic)

---

## 1. High-Level System Architecture

This diagram shows the complete system architecture, including all layers from REST clients through Spring Boot services to external providers and databases.

```mermaid
graph TB
    subgraph "External Clients"
        Client["REST API Client"]
    end
    
    subgraph "Spring Boot Application"
        API["NotificationController"]
        
        subgraph "Service Layer"
            NS["NotificationService"]
            NLS["NotificationLogService"]
            PSS["ProviderSelectionService"]
            CCS["ConfigCacheService"]
        end
        
        subgraph "Factory Pattern"
            CF["ChannelFactory"]
            PF["ProviderFactory"]
        end
        
        subgraph "Channel Implementations"
            ECS["EmailChannelService"]
            SMCS["SmsChannelService"]
            WACS["WhatsAppChannelService"]
        end
        
        subgraph "Provider Implementations"
            AWSES["AwsEmailProviderService"]
            AWSSNS["AwsSmsProviderService"]
            TWILIO["TwilioSmsProviderService"]
            TWWA["TwilioWhatsAppProviderService"]
        end
        
        subgraph "Data Access"
            REPO["Repositories"]
            NLR["NotificationLogRepository"]
            UR["UserRepository"]
            CR["ConfigRepository"]
            NTR["NotificationTemplateRepository"]
        end
    end
    
    subgraph "External Services"
        AWS["AWS Services<br/>SES / SNS"]
        TWIL["Twilio APIs<br/>SMS / WhatsApp"]
    end
    
    subgraph "Data Storage"
        DB[("MySQL Database<br/>Notification Logs")]
        CACHE[("Cache<br/>Provider Config")]
    end
    
    Client -->|REST POST/GET| API
    API --> NS
    
    NS --> CF
    NS --> NLS
    NS --> REPO
    
    CF --> ECS
    CF --> SMCS
    CF --> WACS
    
    ECS --> PF
    SMCS --> PF
    WACS --> PF
    
    PF --> AWSES
    PF --> AWSSNS
    PF --> TWILIO
    PF --> TWWA
    
    ECS --> AWSES
    SMCS --> AWSSNS
    SMCS --> TWILIO
    WACS --> TWWA
    
    AWSES -->|HTTPS| AWS
    AWSSNS -->|HTTPS| AWS
    TWILIO -->|HTTPS| TWIL
    TWWA -->|HTTPS| TWIL
    
    NLS --> NLR
    PSS --> CCS
    CCS --> CACHE
    NLR --> DB
    UR --> DB
    CR --> DB
    NTR --> DB
```

### Key Components:
- **NotificationController**: REST API entry point
- **NotificationService**: Core orchestration and deduplication logic
- **ChannelFactory**: Maps notification channels to implementations
- **ProviderFactory**: Maps channels and providers to service implementations
- **Channel Services**: Email, SMS, and WhatsApp implementations
- **Provider Services**: AWS and Twilio integrations
- **Repositories**: Data access layer for persistence
- **External Services**: AWS (SES/SNS) and Twilio APIs
- **Database**: MySQL for notification logs and configuration
- **Cache**: In-memory provider configuration cache

---

## 2. Request Processing Flow

This flowchart illustrates the complete lifecycle of a notification request, including deduplication checks, channel selection, provider fallback, and logging.

```mermaid
flowchart TD
    A["Incoming Notification Request"]
    A --> B["Extract requestId from Request"]
    B --> C{Check if requestId<br/>already processed?}
    
    C -->|Yes - Duplicate Found| D["Return Cached Response"]
    D --> E["Prevent Duplicate Send"]
    
    C -->|No - New Request| F["Determine Notification Channel"]
    F --> G{Valid Channel?}
    
    G -->|No| H["Throw ValidationException"]
    H --> I["Return FAILURE Status"]
    
    G -->|Yes| J["Get ChannelFactory"]
    J --> K["Get Channel Service<br/>Email/SMS/WhatsApp"]
    K --> L["Get Provider from Config"]
    L --> M["Select Enabled Providers<br/>Sorted by Rank"]
    
    M --> N["Provider Fallback Loop"]
    N --> O["Try First Provider"]
    O --> P{Success?}
    
    P -->|Yes| Q["Return Success Result"]
    Q --> R["Log Notification"]
    R --> S["Save to Database"]
    
    P -->|No| T{More Providers?}
    T -->|Yes| U["Try Next Provider<br/>in Fallback"]
    U --> O
    
    T -->|No| V["All Providers Failed"]
    V --> W["Return FAILURE Status"]
    W --> R
    
    S --> X["Return NotificationResponse<br/>with notificationId"]
    W --> X
    E --> X
    I --> X
    
    X --> Y["End"]
    
    style A fill:#4A90E2,color:#fff
    style C fill:#F5A623,color:#000
    style G fill:#F5A623,color:#000
    style E fill:#52C41A,color:#fff
    style N fill:#7B68EE,color:#fff
    style P fill:#F5A623,color:#000
    style T fill:#F5A623,color:#000
    style X fill:#52C41A,color:#fff
```

### Flow Description:

**Request Deduplication:**
- Every request must include a unique `requestId`
- System checks if this `requestId` was previously processed
- If yes, returns cached response (idempotent operation)
- If no, proceeds with new notification

**Channel & Provider Selection:**
- Validates requested notification channel (EMAIL, SMS, WHATSAPP)
- Uses `ChannelFactory` to get appropriate channel service
- Retrieves enabled providers from configuration (cached for performance)
- Providers are sorted by rank/priority

**Provider Fallback Mechanism:**
- Attempts to send via primary provider (highest rank)
- If primary fails, automatically tries secondary provider
- Continues fallback chain until success or all providers exhausted
- Logs all attempts with detailed error information

**Logging & Persistence:**
- All notification attempts are logged to `NotificationLog` entity
- Includes request metadata, channel, provider, status, and timestamps
- Enables audit trail and future status queries

---

## 3. Sequence Diagram – Notification Sending

This sequence diagram shows the detailed interaction flow between components during notification sending.

```mermaid
sequenceDiagram
    participant Client as REST Client
    participant Controller as NotificationController
    participant NotifService as NotificationService
    participant ChannelFactory as ChannelFactory
    participant Channel as Channel Service<br/>Email/SMS/WhatsApp
    participant ProviderFactory as ProviderFactory
    participant Provider as Provider Service<br/>AWS/Twilio
    participant LogService as NotificationLogService
    participant Repo as NotificationLogRepository
    participant ExtAPI as External API<br/>AWS/Twilio
    
    Client->>Controller: POST /send (NotificationRequest)
    activate Controller
    Controller->>NotifService: enqueueNotification(request)
    activate NotifService
    
    Note over NotifService: Check for duplicate<br/>using requestId
    NotifService->>Repo: findByRequestId(requestId)
    Repo-->>NotifService: Optional<NotificationLog>
    
    alt Duplicate Found
        NotifService-->>Controller: Return existing response
    else New Request
        NotifService->>ChannelFactory: getChannel(NotificationChannel)
        activate ChannelFactory
        ChannelFactory-->>NotifService: NotificationChannelService
        deactivate ChannelFactory
        
        NotifService->>Channel: send(request)
        activate Channel
        
        Channel->>ProviderFactory: getProvider(channel, provider)
        activate ProviderFactory
        ProviderFactory-->>Channel: NotificationProviderService
        deactivate ProviderFactory
        
        Channel->>Provider: send(request)
        activate Provider
        Provider->>ExtAPI: HTTPS Request
        activate ExtAPI
        ExtAPI-->>Provider: Success/Failure
        deactivate ExtAPI
        Provider-->>Channel: NotificationSendResult
        deactivate Provider
        
        Channel-->>NotifService: NotificationSendResult
        deactivate Channel
        
        NotifService->>LogService: logNotification(request, result)
        activate LogService
        LogService->>Repo: save(NotificationLog)
        Repo-->>LogService: Saved entity
        deactivate LogService
        
        NotifService-->>Controller: NotificationResponse
    end
    
    deactivate NotifService
    Controller-->>Client: GenericResponse<NotificationResponse>
    deactivate Controller
```

### Interaction Steps:

1. **Request Reception**: Client sends POST request to NotificationController
2. **Deduplication Check**: Verify requestId hasn't been processed before
3. **Channel Resolution**: Get appropriate channel service from factory
4. **Provider Selection**: Get provider service for the channel
5. **External API Call**: Send request to AWS/Twilio with appropriate credentials
6. **Response Reception**: Capture provider response and status
7. **Logging**: Create permanent record in NotificationLog
8. **Response Return**: Return notification result to client

---

## 4. Factory Pattern Implementation

This diagram shows how the Factory Pattern enables flexible and extensible channel/provider selection.

```mermaid
graph LR
    subgraph "Factory Pattern Implementation"
        CF["<b>ChannelFactory</b><br/>Maps: NotificationChannel<br/>→ NotificationChannelService"]
        EM["<b>EMAIL Channel</b><br/>EmailChannelService"]
        SM["<b>SMS Channel</b><br/>SmsChannelService"]
        WA["<b>WHATSAPP Channel</b><br/>WhatsAppChannelService"]
        
        CF -->|NotificationChannel.EMAIL| EM
        CF -->|NotificationChannel.SMS| SM
        CF -->|NotificationChannel.WHATSAPP| WA
    end
    
    subgraph "Provider Selection & Factory"
        DIR["Request specifies<br/>Channel & Provider"]
        PSS["ProviderSelectionService<br/>Fetches config from cache"]
        PF["<b>ProviderFactory</b><br/>Maps: Channel + Provider<br/>→ NotificationProviderService"]
        
        DIR --> PSS
        PSS --> PF
    end
    
    subgraph "Email Providers"
        AWSE["AWS SES<br/>AwsEmailProviderService"]
        PME["Postmark<br/>PostmarkEmailProviderService"]
        PF -->|AWS_SES| AWSE
        PF -->|POSTMARK| PME
    end
    
    subgraph "SMS Providers"
        AWSS["AWS SNS<br/>AwsSmsProviderService"]
        TWSMS["Twilio SMS<br/>TwilioSmsProviderService"]
        PF -->|AWS_SNS| AWSS
        PF -->|TWILIO| TWSMS
    end
    
    subgraph "WhatsApp Providers"
        TWWA["Twilio WhatsApp<br/>TwilioWhatsAppProviderService"]
        PF -->|TWILIO_WHATSAPP| TWWA
    end
    
    style CF fill:#4A90E2,color:#fff
    style PF fill:#7B68EE,color:#fff
    style DIR fill:#F5A623,color:#000
```

### Benefits:

- **Loose Coupling**: Channels and providers are loosely coupled
- **Extensibility**: Easy to add new channels or providers
- **Maintainability**: Changes to implementations don't affect factory
- **Single Responsibility**: Each factory handles one type of mapping
- **Runtime Selection**: Dynamic provider/channel selection based on configuration

### Example Usage:

```java
// Get channel service
NotificationChannelService channel = channelFactory.getChannel(NotificationChannel.EMAIL);

// Get provider service
NotificationProviderService provider = providerFactory.getProviderService(
    NotificationChannel.EMAIL, 
    NotificationProvider.AWS_SES
);
```

---

## 5. Service Layer Architecture

This diagram shows the complete service layer with all dependencies and interactions.

```mermaid
graph TB
    subgraph "External Integrations"
        AWS["AWS SDK<br/>SES / SNS"]
        TWILIO["Twilio SDK<br/>SMS / WhatsApp"]
        MYSQL["MySQL Driver"]
    end
    
    subgraph "Core Domain - Notification Service"
        NS["NotificationService<br/>(Main Orchestrator)"]
        NLS["NotificationLogService<br/>(Logging & Auditing)"]
        PSS["ProviderSelectionService<br/>(Provider Selection)"]
        CCS["ConfigCacheService<br/>(Configuration Cache)"]
        EHO["GlobalExceptionHandler<br/>(Error Handling)"]
    end
    
    subgraph "Abstraction Layer - Channels"
        IFACE["NotificationChannelService<br/>(Interface)"]
        ECS["EmailChannelService"]
        SMCS["SmsChannelService"]
        WACS["WhatsAppChannelService"]
        
        IFACE --> ECS
        IFACE --> SMCS
        IFACE --> WACS
    end
    
    subgraph "Abstraction Layer - Providers"
        PFACE["NotificationProviderService<br/>(Interface)"]
        AWSE["AwsEmailProviderService"]
        AWSS["AwsSmsProviderService"]
        TWSMS["TwilioSmsProviderService"]
        TWWA["TwilioWhatsAppProviderService"]
        
        PFACE --> AWSE
        PFACE --> AWSS
        PFACE --> TWSMS
        PFACE --> TWWA
    end
    
    subgraph "Data Access Layer"
        NLR["NotificationLogRepository"]
        UR["UserRepository"]
        CR["ConfigRepository"]
        NTR["NotificationTemplateRepository"]
    end
    
    subgraph "Factories"
        CF["ChannelFactory"]
        PF["ProviderFactory"]
    end
    
    NS -->|Uses| CF
    NS -->|Uses| PF
    NS -->|Uses| NLS
    NS -->|Uses| PSS
    
    CF -->|Creates| IFACE
    PF -->|Creates| PFACE
    
    ECS -->|Delegates to| AWSE
    SMCS -->|Delegates to| AWSS
    SMCS -->|Delegates to| TWSMS
    WACS -->|Delegates to| TWWA
    
    PSS -->|Uses| CCS
    CCS -->|Uses| CR
    
    NLS -->|Uses| NLR
    NS -->|Uses| NLR
    NS -->|Uses| UR
    
    AWSE -->|Calls| AWS
    AWSS -->|Calls| AWS
    TWSMS -->|Calls| TWILIO
    TWWA -->|Calls| TWILIO
    
    NLR -->|Persists| MYSQL
    UR -->|Persists| MYSQL
    CR -->|Persists| MYSQL
    NTR -->|Persists| MYSQL
    
    EHO -->|Catches Exceptions| NS
    
    style NS fill:#4A90E2,color:#fff
    style IFACE fill:#52C41A,color:#fff
    style PFACE fill:#52C41A,color:#fff
    style CF fill:#722ED1,color:#fff
    style PF fill:#722ED1,color:#fff
    style EHO fill:#F5A623,color:#000
```

### Service Responsibilities:

**NotificationService**
- Main orchestrator and entry point
- Handles request deduplication
- Routes to appropriate channels
- Manages notification lifecycle

**NotificationLogService**
- Persists notification audit logs
- Tracks delivery status
- Enables notification history queries

**ProviderSelectionService**
- Retrieves provider configurations
- Filters enabled providers
- Sorts by priority/rank

**ConfigCacheService**
- Caches provider configurations
- Reduces database queries
- Improves performance

**GlobalExceptionHandler**
- Centralized error handling
- Custom exception translation
- Standardized error responses

---

## 6. REST API Endpoints

This diagram shows the REST API structure and data models.

```mermaid
graph TB
    Client["REST API Client"]
    
    subgraph "NotificationController<br/>/notification-service"
        subgraph "POST Endpoints"
            EP1["POST /send<br/>sendNotification()"]
        end
        
        subgraph "GET Endpoints"
            EP2["GET /user/{id}<br/>getUserNotifications()"]
            EP3["GET /status/{id}<br/>getStatus()"]
        end
    end
    
    subgraph "Request & Response Models"
        REQ["NotificationRequest<br/>- channel<br/>- requestId<br/>- recipient<br/>- content"]
        RESP["NotificationResponse<br/>- notificationId<br/>- status"]
        STATUS["NotificationStatus<br/>- notificationId<br/>- deliveryStatus<br/>- timestamp"]
        USER["UserNotification<br/>- userId<br/>- notifications[]"]
    end
    
    subgraph "Return Values"
        GR["GenericResponse<T><br/>- success: boolean<br/>- data: T<br/>- error: String"]
    end
    
    Client -->|POST| EP1
    Client -->|GET| EP2
    Client -->|GET| EP3
    
    EP1 --> REQ
    EP1 --> RESP
    EP1 --> GR
    
    EP2 --> USER
    EP2 --> GR
    
    EP3 --> STATUS
    EP3 --> GR
    
    style EP1 fill:#52C41A,color:#fff
    style EP2 fill:#1890FF,color:#fff
    style EP3 fill:#1890FF,color:#fff
    style GR fill:#722ED1,color:#fff
```

### Endpoints Summary:

**POST /notifications/send**
- Send a new notification
- Request: `NotificationRequest` with channel, recipient, content, requestId
- Response: `NotificationResponse` with unique notificationId and status

**GET /notifications/user/{userId}**
- Retrieve all notifications for a user
- Returns: `UserNotification` with list of notification logs

**GET /notifications/status/{notificationId}**
- Check delivery status of specific notification
- Returns: `NotificationStatus` with current delivery state

---

## 7. Database Schema Overview

This section provides an overview of the core database entities and their relationships.

### Core Entities

#### NOTIFICATION_LOG (Primary Audit Entity)
The central entity for tracking all notification attempts and delivery status.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | bigint | PK | Primary key, auto-incremented |
| `notification_id` | string | UK | UUID unique identifier for tracking |
| `request_id` | string | UK | Client-provided ID for deduplication |
| `user_id` | bigint | FK | Reference to USER entity |
| `channel` | string | - | EMAIL, SMS, or WHATSAPP |
| `provider` | string | - | AWS_SES, TWILIO, AWS_SNS, POSTMARK |
| `recipient` | string | - | Email address, phone number, or WhatsApp ID |
| `subject` | string | - | Subject/message title |
| `body` | text | - | Message content |
| `state` | string | - | SUCCESS, FAILURE, PENDING |
| `error_message` | string | - | Error details if failed |
| `created_at` | timestamp | - | Automatic creation timestamp |
| `updated_at` | timestamp | - | Automatic update timestamp |

**Purpose**: Maintains complete audit trail of all notification attempts for compliance, debugging, and status tracking.

---

#### USER Entity
Stores recipient information for notification tracking.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | bigint | PK | Primary key, auto-incremented |
| `name` | string | - | User's full name |
| `email` | string | UK | Unique email address |
| `phone` | string | UK | Unique phone number |
| `active` | boolean | - | User account status |
| `created_at` | timestamp | - | Account creation date |

**Purpose**: Maintains user profile information referenced by notification logs.

---

#### NOTIFICATION_TEMPLATE Entity
Pre-defined message templates with variable placeholders (prepared for future template-based notifications).

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | bigint | PK | Primary key, auto-incremented |
| `template_name` | string | UK | Unique template identifier |
| `channel` | string | - | EMAIL, SMS, or WHATSAPP |
| `subject` | string | - | Email/notification subject |
| `body` | text | - | Template body with placeholders {{var}} |
| `template_content` | text | - | Full template JSON |
| `status` | string | - | ACTIVE, INACTIVE, DEPRECATED |
| `created_at` | timestamp | - | Template creation date |

**Purpose**: Stores reusable message templates for template-based notifications (future feature).

**Example Template**:
```
Template Name: WELCOME_EMAIL
Channel: EMAIL
Subject: Welcome {{userName}}!
Body: Dear {{userName}}, thank you for joining us...
```

---

#### CONFIG Entity
Dynamic configuration storage for provider settings and ranks.

| Field | Type | Constraints | Description |
|-------|------|-------------|-------------|
| `id` | bigint | PK | Primary key, auto-incremented |
| `config_key` | string | UK | Configuration key identifier |
| `config_value` | text | - | Configuration value (often JSON) |
| `description` | string | - | Configuration description |
| `created_at` | timestamp | - | Configuration creation date |
| `updated_at` | timestamp | - | Last modification date |

**Purpose**: Stores dynamic configurations without code changes, including provider rankings and enabled states.

**Example Configuration**:
```json
{
  "config_key": "EMAIL_PROVIDERS",
  "config_value": {
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

---

### Entity Relationships

```
USER (1) ──────────────────── (N) NOTIFICATION_LOG
         
NOTIFICATION_TEMPLATE (1) ──────────────────── (N) NOTIFICATION_LOG (optional)

CONFIG (stores) provider rankings and settings for use by ProviderSelectionService
```

### Indexing Strategy

For optimal query performance, create indexes on:

```sql
-- Deduplication lookup
CREATE INDEX idx_notification_log_request_id ON notification_log(request_id);

-- User history queries
CREATE INDEX idx_notification_log_user_id ON notification_log(user_id);

-- Status tracking
CREATE INDEX idx_notification_log_notification_id ON notification_log(notification_id);

-- Provider performance analysis
CREATE INDEX idx_notification_log_provider ON notification_log(provider, state);

-- Time-based queries
CREATE INDEX idx_notification_log_created_at ON notification_log(created_at);
```

---

### Database Schema Diagram

```mermaid
erDiagram
    NOTIFICATION_LOG ||--o{ USER : "references"
    NOTIFICATION_LOG ||--o{ NOTIFICATION_TEMPLATE : "uses"
    
    NOTIFICATION_LOG {
        bigint id PK
        string notification_id UK
        string request_id UK
        bigint user_id FK
        string channel
        string provider
        string recipient
        string subject
        string body
        string state
        string error_message
        timestamp created_at
        timestamp updated_at
    }
    
    USER {
        bigint id PK
        string email UK
        string phone UK
        string name
        boolean active
        timestamp created_at
    }
    
    NOTIFICATION_TEMPLATE {
        bigint id PK
        string template_name UK
        string channel
        string subject
        string body
        text template_content
        string status
        timestamp created_at
    }
    
    CONFIG {
        bigint id PK
        string config_key UK
        text config_value
        string description
        timestamp created_at
        timestamp updated_at
    }
```

### Data Retention & Cleanup

- **NOTIFICATION_LOG**: Keep for 90 days or longer for compliance
- **USER**: Keep indefinitely unless user requests deletion
- **NOTIFICATION_TEMPLATE**: Keep active/deprecated versions for reference
- **CONFIG**: Keep as is, configurations are historical

**Cleanup Strategy**:
```sql
-- Archive old notification logs (quarterly)
DELETE FROM notification_log WHERE created_at < DATE_SUB(NOW(), INTERVAL 90 DAY);

-- Verify user still active before cleanup
DELETE FROM user WHERE active = false AND updated_at < DATE_SUB(NOW(), INTERVAL 1 YEAR);
```

---

## 8. Provider Fallback Logic

Detailed breakdown of the intelligent provider fallback mechanism.

```mermaid
graph TD
    A["Configure Providers for Channel"]
    A --> B["Email Channel Providers"]
    B --> B1["1. AWS SES<br/>Rank: 1<br/>Enabled: true"]
    B --> B2["2. Postmark<br/>Rank: 2<br/>Enabled: true"]
    
    A --> C["SMS Channel Providers"]
    C --> C1["1. Twilio SMS<br/>Rank: 1<br/>Enabled: true"]
    C --> C2["2. AWS SNS<br/>Rank: 2<br/>Enabled: true"]
    
    A --> D["WhatsApp Channel Providers"]
    D --> D1["1. Twilio WhatsApp<br/>Rank: 1<br/>Enabled: true"]
    
    B1 --> E["Fallback Chain for Email"]
    B2 --> E
    
    E --> F["Attempt Provider 1<br/>AWS SES"]
    F --> G{Request Status}
    
    G -->|Success| H["✓ Delivery Complete<br/>Log with provider ID<br/>Return SUCCESS"]
    
    G -->|Failure| I{More Providers<br/>Available?}
    I -->|Yes| J["Attempt Provider 2<br/>Postmark"]
    J --> K{Request Status}
    K -->|Success| H
    K -->|Failure| L{More Providers<br/>Available?}
    
    L -->|No| M["✗ All Providers Failed<br/>Log failure<br/>Return FAILURE"]
    L -->|Yes| N["Try Next Provider"]
    
    H --> O["Return Notification ID<br/>& Status to Client"]
    M --> O
    
    style F fill:#FFA500,color:#000
    style J fill:#FFA500,color:#000
    style H fill:#52C41A,color:#fff
    style M fill:#FF4444,color:#fff
    style O fill:#1890FF,color:#fff
```

### Fallback Mechanism Benefits:

- **High Availability**: Service continues if primary provider fails
- **Automatic Retry**: No manual intervention needed
- **Transparent to Client**: Client sees success if any provider succeeds
- **Configurable Priority**: Administrators control provider ranking
- **Complete Logging**: All attempts logged for debugging

### Configuration Example:

```json
{
  "group": "CHANNEL_PROVIDERS",
  "key": "EMAIL",
  "value": {
    "providers": [
      {
        "name": "AWS_SES",
        "enabled": true,
        "rank": 1,
        "config": {
          "region": "us-east-1",
          "from_address": "noreply@example.com"
        }
      },
      {
        "name": "POSTMARK",
        "enabled": true,
        "rank": 2,
        "config": {
          "api_key": "xxx-xxx-xxx"
        }
      }
    ]
  }
}
```

---

## Design Patterns Used

### 1. Factory Pattern
- **ChannelFactory**: Creates channel implementations dynamically
- **ProviderFactory**: Creates provider implementations dynamically
- Enables loose coupling and easy extensibility

### 2. Strategy Pattern
- **NotificationChannelService**: Strategy interface for channels
- **NotificationProviderService**: Strategy interface for providers
- Allows runtime selection of implementation

### 3. Repository Pattern
- **Spring Data JPA Repositories**: Abstraction for data access
- Enables easy switching between different persistence layers

### 4. Dependency Injection
- Spring Framework's IoC container manages all dependencies
- Constructor injection for required dependencies
- Loose coupling between components

### 5. Template Method Pattern
- **BaseEntity**: Abstract base class with audit fields
- **GlobalExceptionHandler**: Centralized exception handling

### 6. Observer Pattern (Implicit)
- Configuration changes can trigger cache invalidation
- Providers can be monitored for availability

---

## Data Flow Summary

```
Client Request
    ↓
NotificationController (REST endpoint)
    ↓
NotificationService (Orchestration)
    ├→ Check Deduplication (RequestId lookup)
    ├→ ChannelFactory (Get channel service)
    ├→ ProviderSelectionService (Get providers)
    ├→ Provider Fallback Loop
    │   ├→ Try Primary Provider
    │   ├→ If fail, try Secondary Provider
    │   └→ Continue until success/all fail
    ├→ NotificationLogService (Log attempt)
    └→ Return Response
    ↓
Database (Persist NotificationLog)
    ↓
Client Response
```

---

## Scalability Considerations

1. **Database Indexing**: Create indexes on requestId and userId for fast lookups
2. **Connection Pooling**: HikariCP handles database connections efficiently
3. **Provider Caching**: ConfigCacheService reduces database queries
4. **Async Processing**: Future Kafka integration for asynchronous sending
5. **Load Balancing**: Multiple instances can handle concurrent requests
6. **Provider Rate Limiting**: Monitor and respect provider rate limits

---

## Security Considerations

1. **Authentication**: Validate API requests (implement OAuth2/JWT)
2. **Authorization**: Verify users can only access their notifications
3. **Data Encryption**: Encrypt sensitive data (phone numbers, email addresses)
4. **Credential Management**: Store provider credentials securely (AWS Secrets Manager)
5. **Input Validation**: Validate all incoming request data
6. **HTTPS Only**: All external communications are HTTPS
7. **Audit Logging**: Complete audit trail in NotificationLog table

---

## Monitoring & Observability

1. **Metrics**: Track success/failure rates per provider
2. **Logging**: Structured logging with SLF4J/Logback
3. **Health Checks**: Spring Actuator provides application health endpoints
4. **Provider Health**: Monitor provider connectivity and availability
5. **Database Performance**: Monitor query performance and connection pool

---

## Future Enhancements

1. **Template-Based Notifications**: Support pre-configured message templates
2. **Kafka Integration**: Asynchronous notification processing
3. **Rate Limiting**: Per-user and per-provider rate limits
4. **Webhook Callbacks**: Provider delivery confirmations via webhooks
5. **Batch Processing**: Send multiple notifications in single request
6. **Notification Scheduling**: Schedule notifications for future delivery
7. **A/B Testing**: Compare provider performance
8. **Admin Dashboard**: Web UI for configuration management

---

**Document Version**: 1.0  
**Last Updated**: April 2026  
**Status**: Active
