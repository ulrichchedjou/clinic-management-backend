---
description: Repository Information Overview
alwaysApply: true
---

# Clinic Management System Information

## Summary
A Spring Boot application for clinic management that provides features for appointment scheduling, patient management, doctor scheduling, chat functionality, medical records, and reporting. The system includes authentication, real-time notifications, and monitoring capabilities.

## Structure
- **src/main/java**: Core application code with controllers, services, repositories, and models
- **src/main/resources**: Configuration files and static resources
- **src/test**: Test classes for the application
- **docs**: Documentation files including monitoring guide

## Language & Runtime
**Language**: Java
**Version**: Java 21
**Build System**: Maven
**Package Manager**: Maven

## Dependencies
**Main Dependencies**:
- Spring Boot 3.5.3 (Web, WebFlux, Security, JPA, WebSocket)
- PostgreSQL (Database)
- Redis (Caching and messaging)
- JWT (Authentication)
- Thymeleaf (Templates)
- MapStruct 1.5.5.Final (Object mapping)
- Micrometer (Metrics)
- iText 5.5.13.3 (PDF generation)
- Apache POI 5.2.3 (Excel export)

**Development Dependencies**:
- Spring Boot Test
- H2 Database (Testing)
- Lombok (Code generation)
- JUnit 5 (Testing)

## Build & Installation
```bash
mvn clean install
mvn spring-boot:run
```

## Testing
**Framework**: JUnit 5 with Spring Boot Test
**Test Location**: src/test/java
**Configuration**: Spring Boot Test annotations
**Run Command**:
```bash
mvn test
```

## API Documentation
**Framework**: SpringDoc OpenAPI
**Access URL**: /swagger-ui.html
**Configuration**: OpenAPIConfig class

## Monitoring & Metrics
**Framework**: Spring Boot Actuator with Micrometer
**Endpoints**:
- /management/health
- /management/metrics
- /management/prometheus
  **Custom Metrics**: User logins, appointments statistics
  **Reporting**: PDF and Excel export capabilities

## Features
- **Authentication**: JWT-based authentication with refresh tokens
- **Chat System**: Real-time messaging with WebSockets
- **Appointment Management**: Scheduling with status tracking
- **Medical Records**: Patient history and prescription management
- **Reporting**: Statistical reports with export capabilities
- **Email Notifications**: SMTP integration for notifications