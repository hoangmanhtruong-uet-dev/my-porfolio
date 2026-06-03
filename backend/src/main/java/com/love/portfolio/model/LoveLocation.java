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

    @Column(columnDefinition = "TEXT")
    private String description;

    private double lat;
    private double lon;

    // Danh mục: food, date, travel
    private String category;

    // Ngày tới thăm (format: yyyy-MM-dd)
    private String visitDate;

    // URL ảnh kỷ niệm tại địa điểm này
    @Column(columnDefinition = "TEXT")
    private String photoUrl;

    // Thứ tự thời gian (để vẽ Polyline theo đúng trình tự)
    private Integer sortOrder;
}
