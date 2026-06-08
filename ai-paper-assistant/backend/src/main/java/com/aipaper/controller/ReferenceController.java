package com.aipaper.controller;

import com.aipaper.dto.ReferenceFormatRequest;
import com.aipaper.dto.ReferenceFormatResult;
import com.aipaper.model.User;
import com.aipaper.repository.UserRepository;
import com.aipaper.service.ReferenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reference")
public class ReferenceController {

    private final ReferenceService referenceService;
    private final UserRepository userRepository;

    public ReferenceController(ReferenceService referenceService,
                                UserRepository userRepository) {
        this.referenceService = referenceService;
        this.userRepository = userRepository;
    }

    @PostMapping("/format")
    public ResponseEntity<?> format(@RequestBody ReferenceFormatRequest request) {
        try {
            Long userId = getCurrentUserId();
            ReferenceFormatResult result = referenceService.format(request, userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "格式化失败：" + e.getMessage()));
        }
    }

    @GetMapping("/standards")
    public ResponseEntity<?> getStandards() {
        try {
            List<String> standards = referenceService.getSupportedStandards();
            return ResponseEntity.ok(Map.of("standards", standards));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "获取支持的标准列表失败：" + e.getMessage()));
        }
    }

    /**
     * 从 SecurityContextHolder 获取当前认证用户名，并查询对应的 userId
     */
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户未登录"));
        return user.getId();
    }
}
