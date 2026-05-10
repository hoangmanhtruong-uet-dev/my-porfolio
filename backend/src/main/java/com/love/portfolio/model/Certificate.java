package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String imageUrl;
}
