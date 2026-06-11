# Backend Architecture

## Project Structure

```
backend/
├── src/main/java/com/love/portfolio/
│   ├── config/
│   │   ├── WebConfig.java           # CORS + Interceptor configuration
│   │   ├── RateLimitingInterceptor.java  # Rate limiting implementation
│   │   └── SwaggerConfig.java       # OpenAPI/Swagger configuration
│   ├── controller/
│   │   ├── ChatController.java      # Chat message endpoints
│   │   ├── AiController.java        # AI integration endpoints
│   │   ├── CodeLearnController.java # Web scraping endpoints
│   │   └── ...
│   ├── service/
│   │   ├── AIService.java           # Gemini API integration
│   │   ├── ChemistryAIService.java  # Chemistry analysis
│   │   ├── CloudinaryService.java   # Image upload service
│   │   └── LocationService.java     # Location management
│   ├── repository/
│   │   ├── ChatMessageRepository.java
│   │   ├── ChatUserRepository.java
│   │   └── ...
│   ├── model/
│   │   ├── ChatMessage.java
│   │   ├── ChatUser.java
│   │   └── ...
│   ├── dto/
│   │   └── PageRequest.java         # Pagination DTO
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java    # Global error handling
│   │   ├── ResourceNotFoundException.java
│   │   └── UnauthorizedException.java
│   └── PortfolioApplication.java   # Main Spring Boot application
├── src/main/resources/
│   ├── application.properties.example  # Configuration template
│   ├── static/                      # Frontend assets
│   ├── templates/                   # HTML templates
│   └── database/
│       └── schema.sql               # Database schema
├── src/test/java/
│   ├── controller/
│   │   └── ChatControllerTest.java  # Unit tests
│   └── ...
├── pom.xml                          # Maven dependencies
├── Dockerfile                       # Docker build configuration
├── API_DOCUMENTATION.md             # API reference
├── ARCHITECTURE.md                  # This file
└── maven_output.log                 # Build logs
```

## Technology Stack

### Core Framework
- **Spring Boot 3.2.2** - Modern Java framework
- **Java 21** - Latest LTS version
- **Spring Data JPA** - Database ORM
- **Spring Web** - REST API support

### Database
- **MySQL** (Aiven managed service)
- **HikariCP** - Connection pooling (10 max, 5 min connections)

### External Services
- **Gemini API** - AI/LLM integration
- **Cloudinary** - Image upload & storage
- **JSoup** - Web scraping for code learning

### Security & Quality
- **Spring Security** - Authentication framework
- **Validation** - Input validation with @Valid annotations
- **Rate Limiting** - Guava RateLimiter (100 req/min per IP)
- **Error Handling** - Global exception handler with proper HTTP status codes

### Monitoring & Documentation
- **Spring Actuator** - Health checks & metrics
- **Springdoc OpenAPI** - Swagger/OpenAPI documentation
- **SLF4J + Logback** - Structured logging
- **JUnit 5** - Unit testing framework

## Key Features

### 1. Error Handling
Global `@RestControllerAdvice` handler for:
- Validation errors (400)
- Authorization failures (401)
- Resource not found (404)
- Rate limit exceeded (429)
- Server errors (500)

All responses include timestamp, status, error type, and details.

### 2. Rate Limiting
- **Limit:** 100 requests per minute per IP
- **Implementation:** Guava RateLimiter with LoadingCache
- **Exclusions:** Health checks, Swagger UI, API docs
- **Response:** 429 status when exceeded

### 3. Pagination
- `PageRequest` DTO with validation
- Page numbers: 0-based
- Page size: 1-100 items (default 20)
- Prevents loading entire large datasets

### 4. Logging
- **Root level:** INFO
- **Application level:** DEBUG
- **Output:** Console + file rotation (logs/app.log, 10MB max)
- **History:** 10 rotated files retained

### 5. CORS Configuration
```java
// Configured origins (update for production):
- http://localhost:3000    // Development
- https://yourdomain.com   // Production

Methods: GET, POST, PUT, DELETE, OPTIONS
Max age: 3600 seconds (1 hour)
Credentials: Enabled
```

### 6. Database Configuration
- **ddl-auto:** `validate` (production safe - no auto schema changes)
- **Connection pool:** HikariCP with 10 max connections
- **Timeout:** 20 seconds
- **SSL:** Required for Aiven MySQL

### 7. API Documentation
- **Swagger UI:** Available at `/swagger-ui.html`
- **OpenAPI JSON:** Available at `/api-docs`
- **Auto-generated:** From code annotations and JavaDoc

## Deployment Checklist

### Pre-deployment
- [ ] Update `application.properties` with production values
- [ ] Rotate all API keys (Gemini, Cloudinary, DB password)
- [ ] Remove `.env` from git history
- [ ] Update CORS origins in `WebConfig.java`
- [ ] Enable HTTPS/SSL certificates

### Production Configuration
```properties
spring.jpa.hibernate.ddl-auto=validate
spring.datasource.hikari.maximum-pool-size=20
logging.level.root=WARN
logging.level.com.love.portfolio=INFO
```

### Docker Build
```bash
docker build -t portfolio-backend:latest .
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://... \
  -e DB_USER=... \
  -e DB_PASS=... \
  -e GEMINI_API_KEY=... \
  portfolio-backend:latest
```

### Monitoring
- Health endpoint: `/actuator/health`
- Metrics endpoint: `/actuator/metrics`
- Set up alerts for:
  - High error rates
  - Database connection pool exhaustion
  - API response time degradation

## Security Best Practices

✅ **Implemented:**
- Input validation on all endpoints
- Rate limiting per IP address
- Proper error handling (no stack traces to clients)
- Structured logging for audit trails
- CORS restrictions
- Connection pooling limits

⚠️ **TODO - High Priority:**
- [ ] JWT authentication instead of role-based
- [ ] Password hashing with BCrypt
- [ ] HTTPS/SSL enforcement
- [ ] API key rotation strategy
- [ ] Secrets manager integration (AWS Secrets, Azure Key Vault)

⚠️ **TODO - Medium Priority:**
- [ ] Request signing/HMAC
- [ ] OWASP dependency scanning
- [ ] Security headers (CSP, HSTS, etc.)
- [ ] SQL injection prevention audit
- [ ] Regular penetration testing

## Performance Optimization

1. **Connection Pooling**
   - Max 10 connections (increase for production)
   - Min 5 connections (warm pool)
   - 20s connection timeout

2. **Caching**
   - Rate limiter caches per IP (10-min expiry)
   - Consider adding Redis for distributed caching

3. **Pagination**
   - Mandatory pagination for list endpoints
   - Default 20 items, max 100 per request

4. **Compression**
   - Gzip compression enabled
   - Min 1KB response size

## Testing

Run tests:
```bash
mvn test
```

Test coverage includes:
- Chat controller (authentication, messaging)
- Error handling scenarios
- Validation edge cases

Expand tests to cover:
- All controllers
- Service layer logic
- Repository queries
- Integration tests with database

## Troubleshooting

### Common Issues

**Rate limit errors (429)**
- Check client IP (X-Forwarded-For header)
- Verify rate limiter configuration in `RateLimitingInterceptor.java`

**Database connection failures**
- Verify SSL certificate path
- Check connection pool limits
- Ensure Aiven MySQL is accepting connections

**API key errors**
- Verify `.env` has correct values
- Check API key hasn't expired/been rotated
- Ensure environment variables are loaded

**Slow responses**
- Check database query performance
- Monitor connection pool saturation
- Review logging overhead (adjust log levels)