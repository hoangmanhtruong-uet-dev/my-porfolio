package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tutor_class")
public class TutorClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Thông tin học sinh ──
    private String name;
    private String subject;
    private String grade;
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    // ── Lịch & tiến độ ──
    private String startDate;
    private String schedule;
    private int total;
    private int completed;
    private String status;          // active | pending | done

    // ── Học phí ──
    private Long price;
    private String payment;         // monthly | per-session | full
    private Long received;

    // ── Tuỳ chỉnh ──
    private String color;

    @Column(columnDefinition = "TEXT")
    private String note;

    // ── Timestamps ──
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
