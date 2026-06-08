package com.aipaper.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "literature")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Literature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(length = 500)
    private String authors;

    @Column(length = 300)
    private String journal;

    @Column(length = 20)
    private String volume;

    @Column(length = 20)
    private String issue;

    @Column(length = 50)
    private String pages;

    @Column(name = "publication_year")
    private Integer year;

    @Column(length = 200)
    private String doi;

    @Column(name = "abstract_text", columnDefinition = "TEXT")
    private String abstractText;

    @Column(name = "full_text", columnDefinition = "CLOB")
    private String fullText;

    @Column(name = "ai_summary", columnDefinition = "TEXT")
    private String aiSummary;

    @Column(length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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
