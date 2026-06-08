package com.aipaper.service.impl;

import com.aipaper.dto.LiteratureDTO;
import com.aipaper.model.Literature;
import com.aipaper.repository.LiteratureRepository;
import com.aipaper.service.AiService;
import com.aipaper.service.LiteratureService;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class LiteratureServiceImpl implements LiteratureService {

    private final LiteratureRepository literatureRepository;
    private final AiService aiService;
    private final Path uploadDir;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB

    public LiteratureServiceImpl(LiteratureRepository literatureRepository,
                                 AiService aiService,
                                 @Value("${app.upload-dir}") String uploadDirPath) {
        this.literatureRepository = literatureRepository;
        this.aiService = aiService;
        this.uploadDir = Paths.get(uploadDirPath).toAbsolutePath().normalize();
    }

    @Override
    public LiteratureDTO upload(MultipartFile file, Long userId) throws Exception {
        // 验证文件类型
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        boolean isPdf = (contentType != null && contentType.equals("application/pdf"))
                || (filename != null && filename.toLowerCase().endsWith(".pdf"));
        if (!isPdf) {
            throw new IllegalArgumentException("仅支持PDF格式文件");
        }

        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("文件大小不能超过50MB");
        }

        // 确保上传目录存在
        Files.createDirectories(uploadDir);

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = UUID.randomUUID().toString() + extension;
        Path targetPath = uploadDir.resolve(storedFilename);

        // 保存文件到磁盘
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 使用PDFBox提取文本
        String extractedText;
        try (PDDocument document = Loader.loadPDF(targetPath.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            extractedText = stripper.getText(document);
        }

        // 正则提取元数据
        String title = extractMetadata(extractedText, "(?i)(?:^|\\n)\\s*([\\u4e00-\\u9fa5A-Za-z][^\\n]{0,200}?)\\s*(?:\\n\\s*\\n|$)");
        String authors = extractAuthors(extractedText);
        String doi = extractDoi(extractedText);
        Integer year = extractYear(extractedText);

        // 如果提取的标题为空，使用文件名作为标题
        if (title == null || title.isBlank()) {
            title = originalFilename != null ? originalFilename.replaceAll("\\.pdf$", "") : "未命名文献";
        }

        // 创建文献实体
        Literature literature = Literature.builder()
                .userId(userId)
                .title(title)
                .authors(authors)
                .doi(doi)
                .year(year)
                .fullText(extractedText)
                .filePath(targetPath.toString())
                .status("PENDING")
                .build();

        literature = literatureRepository.save(literature);

        // 异步生成AI摘要
        final Long litId = literature.getId();
        final String fullText = extractedText;
        new Thread(() -> {
            try {
                String summary = aiService.generateSummary(fullText);
                Literature lit = literatureRepository.findById(litId).orElse(null);
                if (lit != null) {
                    lit.setAiSummary(summary);
                    lit.setStatus("COMPLETED");
                    literatureRepository.save(lit);
                }
            } catch (Exception e) {
                Literature lit = literatureRepository.findById(litId).orElse(null);
                if (lit != null) {
                    lit.setStatus("FAILED");
                    literatureRepository.save(lit);
                }
            }
        });

        return toDTO(literature);
    }

    @Override
    public List<LiteratureDTO> listByUser(Long userId) {
        return literatureRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public LiteratureDTO getById(Long id, Long userId) {
        Literature literature = literatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文献不存在"));

        if (!literature.getUserId().equals(userId)) {
            throw new RuntimeException("无权访问该文献");
        }

        return toDTO(literature);
    }

    @Override
    public void delete(Long id, Long userId) {
        Literature literature = literatureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文献不存在"));

        if (!literature.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该文献");
        }

        // 删除磁盘文件
        if (literature.getFilePath() != null) {
            try {
                Files.deleteIfExists(Paths.get(literature.getFilePath()));
            } catch (IOException e) {
                // 文件删除失败不影响数据库记录删除
            }
        }

        literatureRepository.delete(literature);
    }

    @Override
    public String generateSummary(Long litId, Long userId) throws Exception {
        Literature literature = literatureRepository.findById(litId)
                .orElseThrow(() -> new RuntimeException("文献不存在"));

        if (!literature.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该文献");
        }

        if (literature.getFullText() == null || literature.getFullText().isBlank()) {
            throw new RuntimeException("文献全文为空，无法生成摘要");
        }

        String summary = aiService.generateSummary(literature.getFullText());
        literature.setAiSummary(summary);
        literature.setStatus("COMPLETED");
        literatureRepository.save(literature);

        return summary;
    }

    private LiteratureDTO toDTO(Literature literature) {
        return LiteratureDTO.builder()
                .id(literature.getId())
                .userId(literature.getUserId())
                .title(literature.getTitle())
                .authors(literature.getAuthors())
                .journal(literature.getJournal())
                .volume(literature.getVolume())
                .issue(literature.getIssue())
                .pages(literature.getPages())
                .year(literature.getYear())
                .doi(literature.getDoi())
                .abstractText(literature.getAbstractText())
                .fullText(literature.getFullText())
                .aiSummary(literature.getAiSummary())
                .status(literature.getStatus())
                .filePath(literature.getFilePath())
                .createdAt(literature.getCreatedAt())
                .updatedAt(literature.getUpdatedAt())
                .build();
    }

    /**
     * 通用正则提取单行元数据
     */
    private String extractMetadata(String text, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String result = matcher.group(1).trim();
            return result.length() > 200 ? result.substring(0, 200) : result;
        }
        return null;
    }

    /**
     * 提取作者信息
     */
    private String extractAuthors(String text) {
        // 匹配常见的作者行： "作者：XXX" 或 "Author: XXX"
        Pattern pattern = Pattern.compile("(?i)(?:作者|Author|Authors)[：:]\\s*([^\\n]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            String authors = matcher.group(1).trim();
            return authors.length() > 300 ? authors.substring(0, 300) : authors;
        }
        return null;
    }

    /**
     * 提取DOI
     */
    private String extractDoi(String text) {
        Pattern pattern = Pattern.compile("(10[.][0-9]{4,}(?:[.][0-9]+)*/(?:(?![\"&\\'<>\\s)\\]])\\S)+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 提取年份
     */
    private Integer extractYear(String text) {
        Pattern pattern = Pattern.compile("(?:19|20)\\d{2}");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return null;
    }
}
