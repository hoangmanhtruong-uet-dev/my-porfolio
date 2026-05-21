package com.love.portfolio.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*") // Cân nhắc thay '*' bằng domain frontend của bạn khi deploy
public class AiController {

    @Value("${gemini.api.key}") // Lấy API Key từ biến môi trường hoặc application.properties
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=";

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chatWithAI(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");
        Map<String, String> response = new HashMap<>();

        if (prompt == null || prompt.trim().isEmpty()) {
            response.put("response", "Vui lòng nhập câu hỏi.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // Cấu trúc request cho Gemini API
            Map<String, Object> geminiRequest = new HashMap<>();
            Map<String, Object> content = new HashMap<>();
            content.put("parts", new Object[]{Map.of("text", prompt)});
            geminiRequest.put("contents", new Object[]{content});

            // Gửi request đến Gemini API
            String fullUrl = GEMINI_API_URL + geminiApiKey;
            Map<String, Object> geminiResponse = restTemplate.postForObject(fullUrl, geminiRequest, Map.class);

            // Trích xuất nội dung phản hồi
            if (geminiResponse != null && geminiResponse.containsKey("candidates")) {
                Map<String, Object> candidate = (Map<String, Object>) ((java.util.List) geminiResponse.get("candidates")).get(0);
                Map<String, Object> contentPart = (Map<String, Object>) candidate.get("content");
                String aiText = (String) ((java.util.List) contentPart.get("parts")).get(0).get("text");
                response.put("response", aiText);
            } else {
                response.put("response", "Xin lỗi, tôi không thể trả lời câu hỏi này lúc này. Vui lòng thử lại sau.");
            }
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            response.put("response", "Đã xảy ra lỗi khi kết nối với AI. Vui lòng thử lại sau.");
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/chemistry/research")
    public ResponseEntity<Map<String, String>> getResearchData(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        String type = request.get("type"); // 'ai' or 'deep_learning'
        // TODO: Implement actual logic to fetch/generate research data based on topic and type
        // For now, return dummy data
        return ResponseEntity.ok(Map.of("overview", "Đây là tổng quan về " + topic, "architecture", "Kiến trúc của " + topic + " rất phức tạp.", "use_cases", "Ứng dụng của nó rất rộng rãi.", "limitations", "Tuy nhiên, có một số hạn chế."));
    }
}