package com.love.portfolio.controller;

import com.love.portfolio.service.AIService;
import com.love.portfolio.service.ChemistryAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {"https://my-porfolio-1-b1x3.onrender.com", "http://localhost:3000", "http://localhost:8080"})
public class AIController {

    @Autowired
    private AIService aiService;

    @Autowired
    private ChemistryAIService chemistryAIService;

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chatWithAI(@RequestBody Map<String, String> request) {
        String prompt = request.get("prompt");

        if (prompt == null || prompt.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("response", "Vui lòng nhập câu hỏi."));
        }
        // Sử dụng AIService để có đầy đủ Persona và cấu hình Gemini 1.5 Flash
        String aiResponse = aiService.getAIResponse(prompt);
        return ResponseEntity.ok(Map.of("response", aiResponse));
    }

    @PostMapping("/research")
    public ResponseEntity<Object> getResearchData(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        
        if (topic == null || topic.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("response", "Vui lòng nhập chủ đề nghiên cứu."));
        }

        String type = request.getOrDefault("type", "ai");
        // Gọi service thật thay vì trả về dummy data
        return ResponseEntity.ok(chemistryAIService.researchTopic(topic, type));
    }
}
