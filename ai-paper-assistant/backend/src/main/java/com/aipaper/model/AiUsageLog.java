package com.aipaper.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("ai_usage_log")
public class AiUsageLog {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("action_type")
    private String actionType;

    @TableField("char_count")
    private Integer charCount;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
