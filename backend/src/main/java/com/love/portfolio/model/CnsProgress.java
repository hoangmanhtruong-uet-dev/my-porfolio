package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Lưu tiến độ từng bước của mỗi bài CNS.
 * Khóa duy nhất: (lessonId, stepNum) — upsert khi toggle.
 */
@Entity
@Data
@Table(
    name = "cns_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"lesson_id", "step_num"})
)
public class CnsProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ví dụ: "bai1", "bai2", ..., "bai6" */
    @Column(name = "lesson_id", nullable = false, length = 20)
    private String lessonId;

    /** Số thứ tự bước (1-based) */
    @Column(name = "step_num", nullable = false)
    private int stepNum;

    /** Bước đã hoàn thành chưa */
    private boolean done;

    /** URL ảnh minh chứng (Cloudinary URL hoặc null) */
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;
}
