# Production Deployment Checklist

## ✅ Backend Configuration - COMPLETED

### API & CORS Configuration
- [x] Updated `config/WebConfig.java` with production CORS origins
- [x] Updated `controller/AiController.java` with proper CORS settings
- [x] CORS allows: `https://my-porfolio-1-b1x3.onrender.com`, `localhost:8080`, `localhost:3000`
- [x] Support for PATCH method added

### Environment Variables
- [x] Created `application.properties.example` with all required variables
- [x] Database URL supports environment variables
- [x] Gemini API key configurable via `GEMINI_API_KEY`
- [x] Logging level configurable via environment
- [x] Connection pool size configurable: default 20
- [x] Hibernate DDL set to `validate` (safe for production)

### Docker & Deployment
- [x] Updated `Dockerfile` with production best practices
- [x] Added health check endpoint configuration
- [x] Non-root user (spring:spring) for security
- [x] Optimized JVM flags for container environments
- [x] Support for environment variable injection

### Rate Limiting
- [x] Enhanced `RateLimitingInterceptor` with:
  - 100 requests per minute per IP
  - Support for X-Forwarded-For (Render/proxy)
  - Support for CF-Connecting-IP (Cloudflare)
  - Support for X-Real-IP (Nginx)
  - Proper cache expiration (15 minutes)
  - 10000 IP cache size

### Frontend Configuration
- [x] Updated `assets/js/config.js` with production API endpoint detection
- [x] BASE_URL uses monolith pattern (empty string for same domain)
- [x] Fallback to localhost:8080 for local development
- [x] Removed external image URLs (replaced with data URIs)

## 📋 Pre-Deployment Tasks - TO DO

### Before First Deploy to Production

**Database Setup**
- [ ] Verify Aiven MySQL instance is running
- [ ] Test connection from local environment
- [ ] Run database migrations if needed
- [ ] Create SSL certificate for MySQL if required
- [ ] Add Render's IP to Aiven firewall whitelist

**Credentials & API Keys**
- [ ] Obtain valid `GEMINI_API_KEY`
- [ ] Verify Aiven database credentials
- [ ] Store all secrets in Render environment (NOT in code)
- [ ] Create `.env` file locally (add to .gitignore)

**Testing**
- [ ] Test all API endpoints locally (`mvn spring-boot:run`)
- [ ] Test CORS requests from different origins
- [ ] Test rate limiting functionality
- [ ] Verify health check endpoint works
- [ ] Test frontend + backend integration

**Render Configuration**
- [ ] Create new Web Service on Render
- [ ] Connect GitHub repository
- [ ] Set Build Command: `cd backend && mvn clean package -DskipTests`
- [ ] Set Start Command: `java $JAVA_OPTS -jar target/portfolio-0.0.1-SNAPSHOT.jar`
- [ ] Configure all environment variables
- [ ] Set health check path: `/actuator/health`

**DNS & Domain**
- [ ] If using custom domain, add CNAME record
- [ ] Verify SSL certificate is issued
- [ ] Update frontend API endpoints if needed

## 🔍 Verification Steps - AFTER DEPLOY

### Immediate Post-Deploy (15 minutes)
- [ ] Application successfully deployed (check Render dashboard)
- [ ] No errors in logs
- [ ] Health check passing: `GET /actuator/health`
- [ ] Homepage loads: `GET /` (HTTP 200)
- [ ] Swagger UI accessible: `GET /swagger-ui.html`

### API Testing (30 minutes)
- [ ] Test core API endpoints
- [ ] Test AI chat endpoint: `POST /api/ai/chat`
- [ ] Test research endpoint: `POST /api/ai/research`
- [ ] Verify CORS headers are correct
- [ ] Test rate limiting (make 101 requests, verify 429 on 101st)

### Frontend Integration (1 hour)
- [ ] Frontend assets load correctly
- [ ] API calls from frontend work
- [ ] No CORS errors in console
- [ ] Images/assets load properly
- [ ] Responsive design works on mobile

### Monitoring (Ongoing)
- [ ] Set up log monitoring
- [ ] Monitor database connection pool
- [ ] Check error rates
- [ ] Monitor response times
- [ ] Set up alerts for failures

## 🚨 Common Issues & Solutions

### Issue: 503 Service Unavailable
**Cause**: Build failed or startup failed
**Solution**: Check logs in Render dashboard for compilation/runtime errors

### Issue: 502 Bad Gateway
**Cause**: Application crashed or not responding
**Solution**: Check health endpoint, restart service

### Issue: CORS errors in browser
**Cause**: Origin not in allowedOrigins list
**Solution**: Update `WebConfig.java` and `AiController.java` with new origin

### Issue: Database connection timeout
**Cause**: Wrong URL, firewall blocking, credentials incorrect
**Solution**: Verify all database environment variables, check Aiven firewall

### Issue: 429 Too Many Requests
**Normal behavior** when rate limit exceeded (100 req/min)
**Solution**: Wait a minute or increase `REQUESTS_PER_MINUTE` if needed

## 📊 Performance Targets

**Expected Performance on Render.com** (Free Tier):
- Page Load Time: < 2 seconds
- API Response Time: < 500ms
- Concurrent Users: 5-10 (with rate limiting)
- Memory Usage: < 350MB
- CPU: Shared instance

**To optimize**:
- Enable caching for static assets
- Optimize database queries
- Use connection pooling (already configured)
- Monitor slow queries in logs

## 🔐 Security Verification

- [x] No hardcoded secrets in code
- [x] Environment variables used for all credentials
- [x] HTTPS/TLS enforced (via Render)
- [x] CORS properly restricted
- [x] Rate limiting enabled
- [x] Running as non-root user
- [x] Health check secured if needed

**Additional Security Steps**:
- [ ] Enable HTTPS redirect
- [ ] Add security headers (if needed)
- [ ] Set up Web Application Firewall (WAF) if needed
- [ ] Regular security audits

## 📈 Post-Deployment Optimization

### Week 1
- Monitor logs for errors
- Track performance metrics
- Gather user feedback
- Fix any critical issues

### Week 2-4
- Optimize slow endpoints
- Fine-tune rate limiting if needed
- Update content as needed
- Plan feature additions

## 📞 Support Resources

**If Deployment Fails**:
1. Check build logs in Render dashboard
2. Verify all environment variables are set
3. Test locally with same config
4. Check GitHub for commit history
5. Review `DEPLOYMENT.md` for troubleshooting

**Documentation**:
- Architecture: `backend/ARCHITECTURE.md`
- API Reference: `backend/API_DOCUMENTATION.md`
- Deployment Guide: `backend/DEPLOYMENT.md`
- This Checklist: `backend/PRODUCTION_CHECKLIST.md`

## 🎯 Final Status

**Configuration**: ✅ READY FOR PRODUCTION
**Testing Required**: ⚠️ MUST BE COMPLETED BEFORE DEPLOY
**Deployment**: 📋 FOLLOW STEPS IN "PRE-DEPLOYMENT TASKS"

---

**Last Updated**: 2026-06-10
**Status**: Production-Ready Configuration (Pending Testing & Deployment)