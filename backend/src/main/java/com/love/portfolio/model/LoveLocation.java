package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class LoveLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double lat;
    private double lon;
}
