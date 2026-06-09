package com.aipaper.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("literature")
public class Literature {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("title")
    private String title;

    @TableField("authors")
    private String authors;

    @TableField("journal")
    private String journal;

    @TableField("volume")
    private String volume;

    @TableField("issue")
    private String issue;

    @TableField("pages")
    private String pages;

    @TableField("publication_year")
    private Integer year;

    @TableField("doi")
    private String doi;

    @TableField("abstract_text")
    private String abstractText;

    @TableField("full_text")
    private String fullText;

    @TableField("ai_summary")
    private String aiSummary;

    @TableField("status")
    private String status;

    @TableField("file_path")
    private String filePath;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
