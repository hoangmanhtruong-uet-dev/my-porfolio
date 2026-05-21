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

    private final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";

    public Map<String, String> solveChemistry(String reactants) {
        return solveChemistryWithImage(null, reactants);
    }

    public Map<String, String> solveChemistryWithImage(byte[] imageBytes, String userPrompt) {
        RestTemplate restTemplate = new RestTemplate();
        String url = GEMINI_API_URL + apiKey;

        String systemPrompt = "You are a Chemistry Expert. Analyze the input and provide a solution. " +
                "Format your response as a STRICT JSON object with two fields: " +
                "'balanced' and 'explanation'. " +
                "Do not include any other text or markdown blocks.";

        List<Map<String, Object>> partsList = new ArrayList<>();
        partsList.add(Map.of("text", systemPrompt + " User input: " + (userPrompt != null ? userPrompt : "Analyze the image.")));

        if (imageBytes != null) {
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            partsList.add(Map.of(
                "inline_data", Map.of(
                    "mime_type", "image/jpeg",
                    "data", base64Image
                )
            ));
        }

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of("parts", partsList))
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
                    List<?> contentParts = (List<?>) content.get("parts");
                    Map<?, ?> part = (Map<?, ?>) contentParts.get(0);
                    String result = (String) part.get("text");
                    
                    int start = result.indexOf("{");
                    int end = result.lastIndexOf("}");
                    if (start >= 0 && end > start) {
                        String jsonStr = result.substring(start, end + 1);
                        // Parse JSON string back to Map to ensure it's valid and Spring can re-serialize it safely
                        return new com.fasterxml.jackson.databind.ObjectMapper().readValue(jsonStr, Map.class);
                    }
                }
            }
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            return Map.of("balanced", "Lỗi API Google", "explanation", "Chi tiết: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("balanced", "Lỗi Hệ Thống", "explanation", "Đã có lỗi xảy ra: " + e.getMessage());
        }
        return Map.of("balanced", "Lỗi AI", "explanation", "AI phản hồi không đúng định dạng.");
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

    public Map<String, Object> researchTopic(String topic, String type) {
        RestTemplate restTemplate = new RestTemplate();
        String url = GEMINI_API_URL + apiKey;

        String systemRole = "";
        String schema = "";
        
        if ("chemistry".equalsIgnoreCase(type)) {
            systemRole = "Senior Chemistry Researcher";
            schema = "'overview', 'properties', 'applications', 'safety'";
        } else if ("ai".equalsIgnoreCase(type)) {
            systemRole = "AI Research Scientist";
            schema = "'overview' (algorithm/concept), 'architecture' (how it works), 'use_cases' (practical applications), 'limitations' (current challenges)";
        } else if ("deep_learning".equalsIgnoreCase(type)) {
            systemRole = "Deep Learning Systems Expert";
            schema = "'overview' (system/model), 'optimization' (training/performance), 'hardware' (GPU/TPU requirements), 'future_trends'";
        }

        String prompt = "You are a " + systemRole + ". Provide a detailed academic research report about: " + topic + ". " +
                "Format your response as a STRICT JSON object with these fields: " + schema + ". " +
                "The content must be in Vietnamese and use academic, professional language. " +
                "Do not include any other text or markdown blocks.";

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
                    
                    int start = result.indexOf("{");
                    int end = result.lastIndexOf("}");
                    if (start >= 0 && end > start) {
                        String jsonStr = result.substring(start, end + 1);
                        return new com.fasterxml.jackson.databind.ObjectMapper().readValue(jsonStr, Map.class);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of("overview", "Lỗi khi truy xuất dữ liệu nghiên cứu.");
    }
}
