package com.aipaper.mapper;

import com.aipaper.model.AiUsageLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface AiUsageLogMapper extends BaseMapper<AiUsageLog> {

    default List<AiUsageLog> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime after) {
        QueryWrapper<AiUsageLog> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).ge("created_at", after);
        return selectList(qw);
    }
}
