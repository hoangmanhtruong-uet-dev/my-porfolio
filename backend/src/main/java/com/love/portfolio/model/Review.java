package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String author;
    private String role; // e.g., "Học sinh", "Phụ huynh"
    @Column(columnDefinition = "TEXT")
    private String content;
    private int rating; // 1-5 stars
}
