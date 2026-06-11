# Love Portfolio API Documentation

## Overview
This is the backend API for the Love Portfolio application. The API provides endpoints for managing chat messages, AI interactions, certificates, milestones, and more.

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently using role-based authentication. Use `/api/chat/users/auth` endpoint to authenticate.

## Endpoints

### Chat Management
#### Get All Messages
```
GET /api/chat
```
Returns all chat messages.

#### Send Message
```
POST /api/chat
Content-Type: application/json

{
  "sender": "string",
  "text": "string",
  "timestamp": "ISO-8601 datetime"
}
```

#### Get User Status
```
GET /api/chat/users/status
```
Returns status of male and female chat users.

#### Authenticate User
```
POST /api/chat/users/auth
Content-Type: application/json

{
  "role": "male|female",
  "password": "string"
}
```

#### Update Message Reaction
```
PUT /api/chat/{id}/reaction
Content-Type: application/json

{
  "reaction": "string"
}
```

#### Clear Chat History
```
DELETE /api/chat
```
Deletes all messages. ⚠️ Use with caution!

### AI Services
#### Get AI Response
```
POST /api/ai/chat
Content-Type: application/json

{
  "prompt": "string"
}
```

#### Analyze Chemistry Problem
```
POST /api/ai/chemistry
Content-Type: application/json

{
  "problem": "string"
}
```

### Error Responses

#### 400 Bad Request
```json
{
  "timestamp": "2024-06-10T19:30:00",
  "status": 400,
  "error": "Validation Error",
  "details": {
    "fieldName": "error message"
  }
}
```

#### 401 Unauthorized
```json
{
  "timestamp": "2024-06-10T19:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid credentials"
}
```

#### 404 Not Found
```json
{
  "timestamp": "2024-06-10T19:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

#### 429 Too Many Requests
```json
{
  "error": "Too many requests. Please try again later."
}
```

#### 500 Internal Server Error
```json
{
  "timestamp": "2024-06-10T19:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

## Rate Limiting
API requests are rate-limited to 100 requests per minute per IP address.
When limit is exceeded, you'll receive a 429 status code.

## Swagger/OpenAPI Documentation
Access interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

## Environment Variables Required
```
GEMINI_API_KEY=your_gemini_api_key
DB_URL=jdbc:mysql://host:port/database
DB_USER=username
DB_PASS=password
CLOUDINARY_CLOUD_NAME=your_cloud_name
CLOUDINARY_API_KEY=your_api_key
CLOUDINARY_API_SECRET=your_api_secret
```

## Running Tests
```bash
mvn test
```

## Production Deployment
1. Update `application.properties` with production credentials
2. Set `spring.jpa.hibernate.ddl-auto=validate`
3. Configure CORS with actual frontend domain in `WebConfig.java`
4. Ensure all API keys are stored in environment variables
5. Enable HTTPS/SSL
6. Set up proper logging and monitoring
7. Use a secrets manager (AWS Secrets, Azure Key Vault, etc.)

## Security Considerations
- ✅ Input validation on all endpoints
- ✅ Rate limiting enabled
- ✅ Proper error handling and logging
- ✅ CORS configured (update for production domain)
- ⚠️ TODO: Implement JWT authentication
- ⚠️ TODO: Hash passwords using BCrypt
- ⚠️ TODO: Add HTTPS enforcement