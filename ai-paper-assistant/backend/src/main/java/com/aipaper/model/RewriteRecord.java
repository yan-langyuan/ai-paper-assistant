package com.aipaper.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rewrite_record")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewriteRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "original_text", columnDefinition = "CLOB", nullable = false)
    private String originalText;

    @Column(nullable = false)
    private Double similarity;

    @Column(name = "rewritten_text", columnDefinition = "CLOB")
    private String rewrittenText;

    @Column(name = "scheme_type", length = 20)
    private String schemeType;

    @Column(name = "new_similarity")
    private Double newSimilarity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
