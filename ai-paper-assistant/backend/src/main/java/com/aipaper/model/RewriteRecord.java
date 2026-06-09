package com.aipaper.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("rewrite_record")
public class RewriteRecord {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("original_text")
    private String originalText;

    @TableField("similarity")
    private Double similarity;

    @TableField("rewritten_text")
    private String rewrittenText;

    @TableField("scheme_type")
    private String schemeType;

    @TableField("new_similarity")
    private Double newSimilarity;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
