package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private int time; // minutes

    // Lưu danh sách câu hỏi dưới dạng JSON string trong DB
    @Column(columnDefinition = "LONGTEXT")
    private String questionsJson = "[]";

    // Transient field — không lưu vào DB, chỉ dùng khi serialize/deserialize JSON trả về client
    @Transient
    private List<Question> questions;

    private static final ObjectMapper mapper = new ObjectMapper();

    @PostLoad
    @PostPersist
    @PostUpdate
    public void deserializeQuestions() {
        try {
            if (questionsJson != null && !questionsJson.isBlank()) {
                questions = mapper.readValue(questionsJson,
                        mapper.getTypeFactory().constructCollectionType(List.class, Question.class));
            } else {
                questions = new ArrayList<>();
            }
        } catch (JsonProcessingException e) {
            questions = new ArrayList<>();
        }
    }

    @PrePersist
    @PreUpdate
    public void serializeQuestions() {
        try {
            if (questions != null) {
                questionsJson = mapper.writeValueAsString(questions);
            } else {
                questionsJson = "[]";
            }
        } catch (JsonProcessingException e) {
            questionsJson = "[]";
        }
    }
}
