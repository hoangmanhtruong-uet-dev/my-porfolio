package com.love.portfolio.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Cho phép trình duyệt truy cập ảnh đã upload qua URL /uploads/**
     * Ví dụ: http://localhost:8080/uploads/locket/abc.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Lấy đường dẫn tuyệt đối và chuẩn hóa dấu gạch chéo cho Windows
        String uploadPath = Paths.get("uploads").toAbsolutePath().toString().replace("\\", "/");
        
        // Cấu hình URL /uploads/** trỏ đến thư mục vật lý
        String location = "file:" + uploadPath + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(location)
                .setCachePeriod(0);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/uploads/**")
                .allowedOrigins("*")
                .allowedMethods("GET");
    }
}
