package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Milestone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String date;
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "milestone_images", joinColumns = @JoinColumn(name = "milestone_id"))
    @Column(name = "images")
    private List<String> images; // Lưu danh sách đường dẫn ảnh (Cloudinary URL)
}
