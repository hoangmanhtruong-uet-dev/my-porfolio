package com.love.portfolio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class ChemistryAIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    public String solveChemistry(String reactants) {
        RestTemplate restTemplate = new RestTemplate();
        String url = GEMINI_API_URL + apiKey;

        // System prompt to guide Gemini
        String prompt = "You are a Chemistry Expert. The user provides reactants or a chemical equation. " +
                "If only reactants are provided, predict the products and balance the equation. " +
                "If a full equation is provided, balance it. " +
                "Format your response as a JSON object with two fields: " +
                "'balanced': the fully balanced equation using Unicode subscripts (e.g., H₂O, FeCl₂), " +
                "'explanation': a very brief explanation in Vietnamese (1-2 sentences). " +
                "Do not include any other text. Reactants: " + reactants;

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", prompt)))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            System.out.println("Sending AI request for: " + reactants);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            Map<String, Object> body = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && body != null) {
                System.out.println("AI response received successfully.");
                List<?> candidates = (List<?>) body.get("candidates");
                if (candidates == null || candidates.isEmpty()) {
                    return "{\"balanced\": \"AI từ chối trả lời\", \"explanation\": \"Phương trình này có thể vi phạm quy tắc an toàn hoặc quá phức tạp.\"}";
                }
                Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                List<?> parts = (List<?>) content.get("parts");
                Map<?, ?> part = (Map<?, ?>) parts.get(0);
                String result = (String) part.get("text");
                
                // Clean up markdown if Gemini adds it
                return result.replace("```json", "").replace("```", "").trim();
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("Gemini API Error: " + e.getResponseBodyAsString());
            return "{\"balanced\": \"Lỗi API AI (" + e.getStatusCode() + ")\", \"explanation\": \"Vui lòng kiểm tra lại API Key hoặc quyền truy cập.\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"balanced\": \"Lỗi hệ thống AI\", \"explanation\": \"Đã có lỗi xảy ra trong quá trình xử lý AI: " + e.getMessage() + "\"}";
        }

        return "{\"balanced\": \"Không thể xử lý\", \"explanation\": \"AI không thể tìm ra lời giải cho phương trình này.\"}";
    }

    public String generateQuiz(String topic) {
        RestTemplate restTemplate = new RestTemplate();
        String url = GEMINI_API_URL + apiKey;

        String prompt = "You are a Chemistry Teacher. Generate 5 multiple-choice questions about: " + topic + ". " +
                "Format your response as a JSON array of objects. Each object must have: " +
                "\"question\": the question text, " +
                "\"options\": an array of 4 strings, " +
                "\"correct\": the index of the correct answer (0-3), " +
                "\"explanation\": a brief explanation in Vietnamese. " +
                "Return ONLY the JSON array. Do not include any other text.";

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(Map.of("text", prompt)))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, 
                HttpMethod.POST, 
                entity, 
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            Map<String, Object> body = response.getBody();
            if (response.getStatusCode() == HttpStatus.OK && body != null) {
                List<?> candidates = (List<?>) body.get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                    List<?> parts = (List<?>) content.get("parts");
                    Map<?, ?> part = (Map<?, ?>) parts.get(0);
                    String result = (String) part.get("text");
                    return result.replace("```json", "").replace("```", "").trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[]";
    }
}
