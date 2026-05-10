package com.love.portfolio.controller;

import com.love.portfolio.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AIController {

    @Autowired
    private AIService aiService;

    @PostMapping("/chat")
    public Map<String, String> chatWithAI(@RequestBody Map<String, String> request) {
        String userPrompt = request.get("prompt");
        String response = aiService.getAIResponse(userPrompt);
        return Map.of("response", response);
    }
}
