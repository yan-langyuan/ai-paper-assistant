package com.aipaper.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LiteratureDTO {

    private Long id;

    private Long userId;

    private String title;

    private String authors;

    private String journal;

    private String volume;

    private String issue;

    private String pages;

    private Integer year;

    private String doi;

    private String abstractText;

    private String fullText;

    private String aiSummary;

    private String status;

    private String filePath;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
