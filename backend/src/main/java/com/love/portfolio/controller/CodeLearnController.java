package com.love.portfolio.controller;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/codelearn")
@CrossOrigin(origins = "*")
public class CodeLearnController {

    @GetMapping("/stats/{username}")
    public ResponseEntity<Map<String, Object>> getStats(@PathVariable String username) {
        Map<String, Object> response = new HashMap<>();
        try {
            String url = "https://codelearn.io/profile/" + username;
            // Dùng Jsoup kết nối đến profile
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(10000)
                    .get();

            // Nếu web CodeLearn render SSR (Server-side rendering), ta có thể bóc tách điểm.
            // Nếu là React SPA, Jsoup sẽ không thấy dữ liệu.
            // Đoạn này là ví dụ mẫu để bạn thấy luồng đi của Web Scraping:
            
            response.put("status", "success");
            response.put("username", username);
            response.put("profileUrl", url);
            
            // Giả lập điểm số lấy được sau khi phân tích HTML (Parsing)
            Map<String, Integer> skills = new HashMap<>();
            skills.put("Java", 85);
            skills.put("C++", 70);
            skills.put("Python", 60);
            skills.put("SQL", 90);
            
            response.put("skills", skills);
            response.put("message", "API Cào dữ liệu đã sẵn sàng! Cần cập nhật CSS Selector thực tế từ HTML của CodeLearn.");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Lỗi khi cào dữ liệu CodeLearn: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
