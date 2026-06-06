package com.love.portfolio.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Lưu URL file PDF bài nộp CNS.
 * Khóa duy nhất: slotId (vd: "bai1", "bai2", ...)
 */
@Entity
@Data
@Table(name = "cns_pdf")
public class CnsPdf {

    @Id
    @Column(name = "slot_id", length = 20)
    private String slotId;

    @Column(name = "file_url", columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;
}
