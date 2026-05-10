package com.love.portfolio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Cho phép trình duyệt truy cập ảnh đã upload qua URL /uploads/**
     * Ví dụ: http://localhost:8080/uploads/locket/abc.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Tự động tìm thư mục uploads cho dù chạy từ root hay từ folder backend
        Path path = Paths.get("uploads").toAbsolutePath();
        
        // Nếu không thấy ở folder hiện tại, thử tìm ở folder cha (trường hợp chạy từ folder backend)
        if (!Files.exists(path) && Files.exists(Paths.get("..", "uploads"))) {
            path = Paths.get("..", "uploads").toAbsolutePath();
        }

        String location = path.toUri().toString();
        if (!location.endsWith("/")) {
            location += "/";
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(0);
        
        System.out.println("DEBUG: Uploads folder path: " + path.toString());
        System.out.println("DEBUG: Static resource location: " + location);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Cho phép Frontend truy cập API
        registry.addMapping("/api/**")
                .allowedOrigins("*") // Sau này có thể thay "*" bằng domain thật của frontend
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");

        // Cho phép truy cập ảnh
        registry.addMapping("/uploads/**")
                .allowedOrigins("*")
                .allowedMethods("GET");
    }
}
