package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String text;
    private LocalDateTime timestamp;
    private String reaction;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
