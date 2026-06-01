package com.love.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class PortfolioApplication {
    public static void main(String[] args) {
        // Tự động nạp các biến môi trường từ tệp .env cục bộ nếu có
        try {
            String[] pathsToTry = {".env", "../.env", "backend/.env"};
            for (String pathStr : pathsToTry) {
                var path = Paths.get(pathStr);
                if (Files.exists(path)) {
                    List<String> lines = Files.readAllLines(path);
                    for (String line : lines) {
                        line = line.trim();
                        if (!line.isEmpty() && !line.startsWith("#")) {
                            String[] parts = line.split("=", 2);
                            if (parts.length == 2) {
                                String key = parts[0].trim();
                                String value = parts[1].trim();
                                // Chỉ đặt thuộc tính nếu chưa được đặt ở cấp hệ thống
                                if (System.getProperty(key) == null && System.getenv(key) == null) {
                                    System.setProperty(key, value);
                                }
                            }
                        }
                    }
                    System.out.println(">>> Loaded env variables from: " + path.toAbsolutePath());
                    break;
                }
            }
        } catch (IOException e) {
            // Bỏ qua lỗi nếu không đọc được file .env
        }

        SpringApplication.run(PortfolioApplication.class, args);
    }
}
