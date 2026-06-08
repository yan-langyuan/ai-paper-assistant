package com.aipaper.repository;

import com.aipaper.model.Literature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LiteratureRepository extends JpaRepository<Literature, Long> {

    List<Literature> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Literature> findByUserIdAndStatus(Long userId, String status);

    long countByUserId(Long userId);
}
