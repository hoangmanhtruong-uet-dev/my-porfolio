package com.love.portfolio.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=";

    public String getAIResponse(String userPrompt) {
        RestTemplate restTemplate = new RestTemplate();
        String url = GEMINI_API_URL + apiKey;

        String systemPrompt = "Bạn là trợ lý AI thông minh của Hoàng Mạnh Trường (Mtruong_dev). " +
                "Bạn có kiến thức sâu rộng về Hóa học, Lập trình (Java, Spring Boot, AI, Deep Learning) và các dịch vụ Gia sư của anh Trường. " +
                "Hãy trả lời người dùng một cách thân thiện, chuyên nghiệp bằng tiếng Việt. " +
                "Thông tin thêm: Học phí gia sư khoảng 200k-350k/buổi. Anh Trường học tại UET - ĐHQGHN.";

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", systemPrompt + "\n\nUser: " + userPrompt)
                ))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, 
                new org.springframework.core.ParameterizedTypeReference<Map<String, Object>>() {}
            );

            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("candidates")) {
                List<?> candidates = (List<?>) body.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<?, ?> candidate = (Map<?, ?>) candidates.get(0);
                    Map<?, ?> content = (Map<?, ?>) candidate.get("content");
                    List<?> parts = (List<?>) content.get("parts");
                    Map<?, ?> part = (Map<?, ?>) parts.get(0);
                    return (String) part.get("text");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, mình đang gặp chút trục trặc kỹ thuật. Hãy nhắn lại cho mình sau vài giây nhé!";
        }
        return "Mình chưa hiểu ý bạn lắm, bạn có thể nói rõ hơn không?";
    }
}
