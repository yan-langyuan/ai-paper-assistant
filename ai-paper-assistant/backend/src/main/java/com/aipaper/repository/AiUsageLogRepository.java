package com.aipaper.repository;

import com.aipaper.model.AiUsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AiUsageLogRepository extends JpaRepository<AiUsageLog, Long> {

    List<AiUsageLog> findByUserIdAndCreatedAtAfter(Long userId, LocalDateTime after);
}
