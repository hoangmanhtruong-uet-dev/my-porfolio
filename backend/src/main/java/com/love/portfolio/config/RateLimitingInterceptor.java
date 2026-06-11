package com.love.portfolio.config;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingInterceptor.class);
    private static final int REQUESTS_PER_MINUTE = 100; // Production: Can be increased to 300 if needed
    private static final int CACHE_MAX_SIZE = 10000;

    private final LoadingCache<String, RateLimiter> limiters = CacheBuilder.newBuilder()
            .maximumSize(CACHE_MAX_SIZE)
            .expireAfterAccess(15, TimeUnit.MINUTES)
            .build(new CacheLoader<String, RateLimiter>() {
                @Override
                public RateLimiter load(String key) {
                    return RateLimiter.create(REQUESTS_PER_MINUTE / 60.0);
                }
            });

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String clientIP = getClientIP(request);
        RateLimiter rateLimiter;

        try {
            rateLimiter = limiters.get(clientIP);
        } catch (ExecutionException e) {
            logger.error("Error getting rate limiter for IP: {}", clientIP);
            return true; // Allow request on error
        }

        if (!rateLimiter.tryAcquire()) {
            logger.warn("Rate limit exceeded for IP: {}", clientIP);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Too many requests. Please try again later.\"}");
            return false;
        }

        return true;
    }

    private String getClientIP(HttpServletRequest request) {
        // Support Render & standard proxies
        String clientIP = request.getHeader("X-Forwarded-For");
        if (clientIP == null || clientIP.isEmpty()) {
            clientIP = request.getHeader("CF-Connecting-IP"); // Cloudflare
        }
        if (clientIP == null || clientIP.isEmpty()) {
            clientIP = request.getHeader("X-Real-IP"); // Nginx
        }
        if (clientIP == null || clientIP.isEmpty()) {
            clientIP = request.getRemoteAddr();
        } else {
            // Extract first IP if multiple (X-Forwarded-For can contain multiple IPs)
            clientIP = clientIP.split(",")[0].trim();
        }
        return clientIP;
    }
}