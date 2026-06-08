package com.aipaper.controller;

import com.aipaper.dto.DetectRequest;
import com.aipaper.dto.DetectResult;
import com.aipaper.dto.RewriteRequest;
import com.aipaper.dto.RewriteResult;
import com.aipaper.model.User;
import com.aipaper.repository.UserRepository;
import com.aipaper.service.ParaphraseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/paraphrase")
public class ParaphraseController {

    private final ParaphraseService paraphraseService;
    private final UserRepository userRepository;

    public ParaphraseController(ParaphraseService paraphraseService,
                                 UserRepository userRepository) {
        this.paraphraseService = paraphraseService;
        this.userRepository = userRepository;
    }

    @PostMapping("/detect")
    public ResponseEntity<?> detect(@RequestBody DetectRequest request) {
        try {
            Long userId = getCurrentUserId();
            DetectResult result = paraphraseService.detect(request.getText(), userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "查重检测失败：" + e.getMessage()));
        }
    }

    @PostMapping("/rewrite")
    public ResponseEntity<?> rewrite(@RequestBody RewriteRequest request) {
        try {
            Long userId = getCurrentUserId();
            RewriteResult result = paraphraseService.rewrite(request.getOriginalText(), userId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "降重改写失败：" + e.getMessage()));
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
