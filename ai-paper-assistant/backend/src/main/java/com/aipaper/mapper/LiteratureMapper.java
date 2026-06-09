package com.aipaper.mapper;

import com.aipaper.model.Literature;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface LiteratureMapper extends BaseMapper<Literature> {

    default List<Literature> findByUserIdOrderByCreatedAtDesc(Long userId) {
        QueryWrapper<Literature> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).orderByDesc("created_at");
        return selectList(qw);
    }

    default List<Literature> findByUserIdAndStatus(Long userId, String status) {
        QueryWrapper<Literature> qw = new QueryWrapper<>();
        qw.eq("user_id", userId).eq("status", status);
        return selectList(qw);
    }

    default long countByUserId(Long userId) {
        QueryWrapper<Literature> qw = new QueryWrapper<>();
        qw.eq("user_id", userId);
        return selectCount(qw);
    }
}
