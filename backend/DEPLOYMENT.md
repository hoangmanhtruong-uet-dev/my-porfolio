# Backend Deployment Guide

## Overview
This is a monolithic Spring Boot application that serves both API endpoints and static frontend assets. The application is optimized for deployment on Render.com or similar container platforms.

**Current Production URL**: https://my-porfolio-1-b1x3.onrender.com

## Pre-Deployment Checklist

- [ ] All environment variables are configured
- [ ] Database (Aiven MySQL) is accessible
- [ ] CORS origins are properly set
- [ ] API endpoints are tested locally
- [ ] Frontend assets are included in `src/main/resources/static/`
- [ ] SSL/TLS certificates are valid
- [ ] Rate limiting is configured appropriately
- [ ] Health check endpoint is accessible

## Environment Variables

Set these environment variables in your deployment platform:

```bash
# Database Configuration (Aiven MySQL)
SPRING_DATASOURCE_URL=jdbc:mysql://host:port/database?sslMode=REQUIRED&serverTimezone=UTC
SPRING_DATASOURCE_USERNAME=avnadmin
SPRING_DATASOURCE_PASSWORD=your_password

# Database Connection Pool
DATASOURCE_POOL_SIZE=20

# Hibernate Configuration
HIBERNATE_DDL_AUTO=validate  # NEVER use 'update' in production

# AI Services
GEMINI_API_KEY=your_gemini_api_key

# Logging
LOG_LEVEL_ROOT=INFO
LOG_LEVEL_APP=INFO
LOG_FILE_NAME=logs/app.log

# Show SQL (disable in production)
SHOW_SQL=false

# Port (default 8080, can be overridden)
PORT=8080
```

## Local Development Setup

### Prerequisites
- Java 21 or later
- Maven 3.9+
- MySQL 8.0+ (or use Aiven)

### Steps

1. **Copy example configuration**
   ```bash
   cp backend/src/main/resources/application.properties.example \
      backend/src/main/resources/application.properties
   ```

2. **Update application.properties with your database credentials**
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/love_db
   spring.datasource.username=root
   spring.datasource.password=your_password
   gemini.api.key=your_gemini_api_key
   ```

3. **Build the project**
   ```bash
   cd backend
   mvn clean package
   ```

4. **Run locally**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
   - API: http://localhost:8080/api
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/actuator/health

## Docker Build & Run

### Build Docker Image
```bash
cd backend
docker build -t my-portfolio:latest .
```

### Run Docker Container Locally
```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host:3306/db \
  -e SPRING_DATASOURCE_USERNAME=user \
  -e SPRING_DATASOURCE_PASSWORD=password \
  -e GEMINI_API_KEY=key \
  my-portfolio:latest
```

## Deployment on Render.com

### 1. Create Web Service
- Connect your GitHub repository
- Set Build Command: `cd backend && mvn clean package -DskipTests`
- Set Start Command: `java $JAVA_OPTS -jar target/portfolio-0.0.1-SNAPSHOT.jar`

### 2. Configure Environment Variables
In Render dashboard, set all variables from the "Environment Variables" section above.

### 3. Configure Health Check
- Path: `/actuator/health`
- Check interval: 30 seconds
- Timeout: 5 seconds

### 4. Set Up Database
- Use Aiven MySQL as external database
- Ensure SSL/TLS connection with `sslMode=REQUIRED`
- Configure firewall to allow Render IP

### 5. Custom Domain (Optional)
- Add custom domain in Render settings
- Update CORS origins in code if using different domain
- Update `API_CONFIG.BASE_URL` in frontend config

## Deployment on Other Platforms

### Heroku
```bash
# Login
heroku login

# Create app
heroku create my-portfolio

# Set environment variables
heroku config:set SPRING_DATASOURCE_URL="..."
heroku config:set GEMINI_API_KEY="..."

# Deploy
git push heroku main
```

### AWS ECS/Fargate
1. Push Docker image to ECR
2. Create ECS task definition
3. Configure security groups and networking
4. Set environment variables in task definition

### DigitalOcean App Platform
1. Connect GitHub repository
2. Select Dockerfile for build
3. Set environment variables
4. Configure health checks

## Production Configuration Details

### CORS Settings
The application is configured to accept requests from:
- Production: `https://my-porfolio-1-b1x3.onrender.com`
- Local Development: `http://localhost:8080`, `http://localhost:3000`

**To add more origins**, update:
- `backend/src/main/java/com/love/portfolio/config/WebConfig.java`
- `backend/src/main/java/com/love/portfolio/controller/AiController.java`

### Rate Limiting
- **Limit**: 100 requests per minute per IP
- **Cache expiration**: 15 minutes
- **Response on limit**: HTTP 429 (Too Many Requests)

To adjust:
```java
// In RateLimitingInterceptor.java
private static final int REQUESTS_PER_MINUTE = 100; // Change this value
```

### JVM Optimization
The Dockerfile is configured with:
- Single-threaded garbage collection (UseSerialGC) for small containers
- Reduced stack size (256KB) to save memory
- 70% max heap size for dynamic memory allocation
- Server JVM optimizations enabled

### Database Configuration
- **Connection Pool**: 20 connections (HikariCP)
- **Idle Timeout**: 10 minutes
- **Max Lifetime**: 30 minutes
- **Connection Timeout**: 20 seconds

**Never use `ddl-auto=update` in production**. Always use `validate` to prevent accidental schema changes.

## Health Checks & Monitoring

### Health Check Endpoint
```bash
curl https://my-porfolio-1-b1x3.onrender.com/actuator/health
```

Response:
```json
{
  "status": "UP"
}
```

### Metrics Endpoint
```bash
curl https://my-porfolio-1-b1x3.onrender.com/actuator/metrics
```

### View Logs (Render)
```bash
# Show recent logs
render logs --service my-portfolio

# Stream logs
render logs --service my-portfolio --follow
```

## Troubleshooting

### Issue: Database Connection Timeout
**Solution**: 
- Verify SPRING_DATASOURCE_URL format
- Check if Aiven firewall allows Render IP
- Ensure SSL mode matches certificate requirements

### Issue: CORS Error
**Solution**:
- Verify frontend domain is in CORS allowedOrigins
- Check browser console for exact error message
- Update WebConfig.java with new origin

### Issue: Rate Limiting Too Strict
**Solution**:
- Increase REQUESTS_PER_MINUTE in RateLimitingInterceptor.java
- Rebuild and redeploy

### Issue: Memory Issues
**Solution**:
- Adjust `-XX:MaxRAMPercentage` in Dockerfile
- Monitor memory usage in Render dashboard
- Increase container size if needed

### Issue: API Calls Fail
**Solution**:
- Check GEMINI_API_KEY is set and valid
- Verify API endpoints in browser dev tools
- Check backend logs for detailed errors

## Post-Deployment Steps

1. **Verify Application**
   ```bash
   curl https://my-porfolio-1-b1x3.onrender.com/
   curl https://my-porfolio-1-b1x3.onrender.com/api/health
   curl https://my-porfolio-1-b1x3.onrender.com/swagger-ui.html
   ```

2. **Test Key Endpoints**
   - Homepage loads correctly
   - API endpoints respond
   - WebSocket connections (if applicable)

3. **Monitor**
   - Check application logs for errors
   - Monitor database connection pool
   - Track API response times

4. **Backup Database**
   - Enable automated backups on Aiven
   - Test restore procedures

## Updating Deployment

### Simple Code Changes
```bash
git add .
git commit -m "Description of changes"
git push origin main
# Render auto-redeploys
```

### Environment Variable Changes
- Update in Render dashboard
- Restart service (automatic redeployment)

### Database Schema Changes
1. Create migration script
2. Test in local environment
3. Apply to production database before code deployment

## Security Considerations

- ✅ Disabled SQL in logs (production)
- ✅ Disabled detailed error messages (production)
- ✅ Using HTTPS/TLS for all connections
- ✅ Rate limiting enabled
- ✅ CORS properly configured
- ✅ Running as non-root user in Docker
- ✅ Using environment variables for secrets (never commit credentials)

## Performance Optimization

- ✅ Compression enabled for responses
- ✅ Connection pooling configured
- ✅ Caching enabled for rate limiters
- ✅ Optimized JVM settings
- ✅ Single-threaded GC for small containers

## Rollback Procedure

If deployment has issues:

1. **On Render**: Go to Deployments tab and select previous successful version
2. **Quick rollback**: Push previous commit to main branch

## Support & Documentation

- **API Documentation**: https://my-porfolio-1-b1x3.onrender.com/swagger-ui.html
- **Architecture Guide**: `backend/ARCHITECTURE.md`
- **API Reference**: `backend/API_DOCUMENTATION.md`