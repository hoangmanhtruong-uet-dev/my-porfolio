package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class BucketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    private boolean done;
    private String doneDate;      // "DD/MM/YYYY" khi tick

    @Column(columnDefinition = "TEXT")
    private String photo;         // base64 hoặc Cloudinary URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BucketCategory category;
}
