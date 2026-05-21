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

            response.put("status", "success");
            response.put("username", username);
            response.put("profileUrl", url);
            
            // --- Bắt đầu phần Web Scraping thực tế ---
            // Bạn cần kiểm tra cấu trúc HTML của trang profile CodeLearn để tìm các selector chính xác.
            // Ví dụ: tìm các thẻ div, span, a có class hoặc id chứa thông tin kỹ năng và điểm số.
            Map<String, Integer> skills = new HashMap<>();
            
            // Ví dụ: Giả sử có một cấu trúc như <div class="skill-item"><span class="skill-name">Java</span><span class="skill-score">85</span></div>
            Elements skillElements = doc.select(".skill-item"); // Thay thế bằng CSS selector thực tế
            for (org.jsoup.nodes.Element skillElement : skillElements) {
                String skillName = skillElement.select(".skill-name").text(); // Thay thế bằng CSS selector thực tế
                String skillScoreStr = skillElement.select(".skill-score").text(); // Thay thế bằng CSS selector thực tế
                try {
                    int skillScore = Integer.parseInt(skillScoreStr);
                    skills.put(skillName, skillScore);
                } catch (NumberFormatException ignored) { /* Bỏ qua nếu không phải số */ }
            }
            // --- Kết thúc phần Web Scraping thực tế ---
            
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
