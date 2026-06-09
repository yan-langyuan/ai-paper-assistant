package com.aipaper.controller;

import com.aipaper.dto.LiteratureDTO;
import com.aipaper.mapper.UserMapper;
import com.aipaper.model.User;
import com.aipaper.service.LiteratureService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/literature")
public class LiteratureController {

    private final LiteratureService literatureService;
    private final UserMapper userMapper;

    public LiteratureController(LiteratureService literatureService,
                                 UserMapper userMapper) {
        this.literatureService = literatureService;
        this.userMapper = userMapper;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            Long userId = getCurrentUserId();
            LiteratureDTO dto = literatureService.upload(file, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "上传失败：" + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<?> list() {
        try {
            Long userId = getCurrentUserId();
            List<LiteratureDTO> list = literatureService.listByUser(userId);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "获取文献列表失败：" + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            LiteratureDTO dto = literatureService.getById(id, userId);
            return ResponseEntity.ok(dto);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "获取文献详情失败：" + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            literatureService.delete(id, userId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "删除失败：" + e.getMessage()));
        }
    }

    @PostMapping("/{id}/summary")
    public ResponseEntity<?> generateSummary(@PathVariable Long id) {
        try {
            Long userId = getCurrentUserId();
            String summary = literatureService.generateSummary(id, userId);
            return ResponseEntity.ok(Map.of("summary", summary));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "生成摘要失败：" + e.getMessage()));
        }
    }

    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }
        return user.getId();
    }
}
