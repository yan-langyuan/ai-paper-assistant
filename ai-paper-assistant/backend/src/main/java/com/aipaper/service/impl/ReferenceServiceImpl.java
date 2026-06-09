package com.aipaper.service.impl;

import com.aipaper.dto.ReferenceFormatRequest;
import com.aipaper.dto.ReferenceFormatResult;
import com.aipaper.mapper.LiteratureMapper;
import com.aipaper.model.Literature;
import com.aipaper.service.ReferenceService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReferenceServiceImpl implements ReferenceService {

    private final LiteratureMapper literatureMapper;

    private static final List<String> SUPPORTED_STANDARDS = List.of("GB/T 7714", "APA 7th", "MLA 9th");

    public ReferenceServiceImpl(LiteratureMapper literatureMapper) {
        this.literatureMapper = literatureMapper;
    }

    @Override
    public ReferenceFormatResult format(ReferenceFormatRequest req, Long userId) {
        String standard = req.getStandard();
        if (standard == null || standard.isBlank()) {
            standard = "GB/T 7714";
        }

        final String effectiveStandard = standard;
        if (SUPPORTED_STANDARDS.stream().noneMatch(s -> s.equalsIgnoreCase(effectiveStandard))) {
            throw new RuntimeException("不支持的参考文献格式标准：" + effectiveStandard + "，支持的标准：" + SUPPORTED_STANDARDS);
        }

        // 查询文献
        List<Literature> literatures = literatureMapper.selectBatchIds(req.getLiteratureIds());

        // 归属校验
        for (Literature lit : literatures) {
            if (!lit.getUserId().equals(userId)) {
                throw new RuntimeException("无权访问文献：" + lit.getId());
            }
        }

        // 格式化
        List<String> formattedList = literatures.stream()
                .map(lit -> formatSingle(lit, effectiveStandard))
                .collect(Collectors.toList());

        // 排序
        String sortBy = req.getSortBy();
        if ("pinyin".equalsIgnoreCase(sortBy)) {
            formattedList.sort(Comparator.comparing(this::chineseToPinyin));
        } else if ("alphabet".equalsIgnoreCase(sortBy)) {
            formattedList.sort(String::compareToIgnoreCase);
        }

        return ReferenceFormatResult.builder()
                .formattedList(formattedList)
                .standard(effectiveStandard)
                .build();
    }

    @Override
    public List<String> getSupportedStandards() {
        return SUPPORTED_STANDARDS;
    }

    private String formatSingle(Literature lit, String standard) {
        return switch (standard.toLowerCase()) {
            case "apa 7th" -> formatApa(lit);
            case "mla 9th" -> formatMla(lit);
            default -> formatGbt(lit);
        };
    }

    private String formatGbt(Literature lit) {
        StringBuilder sb = new StringBuilder();
        if (lit.getAuthors() != null && !lit.getAuthors().isBlank()) {
            sb.append(lit.getAuthors().trim());
            if (!lit.getAuthors().trim().endsWith(".") && !lit.getAuthors().trim().endsWith("，")) {
                sb.append(".");
            }
            sb.append(" ");
        }
        sb.append(lit.getTitle() != null ? lit.getTitle().trim() : "未命名文献");
        sb.append("[J]. ");
        if (lit.getJournal() != null && !lit.getJournal().isBlank()) {
            sb.append(lit.getJournal().trim()).append(", ");
        }
        if (lit.getYear() != null) {
            sb.append(lit.getYear());
        }
        boolean hasVolume = lit.getVolume() != null && !lit.getVolume().isBlank();
        boolean hasIssue = lit.getIssue() != null && !lit.getIssue().isBlank();
        if (hasVolume) {
            sb.append(", ").append(lit.getVolume());
            if (hasIssue) {
                sb.append("(").append(lit.getIssue()).append(")");
            }
        } else if (hasIssue) {
            sb.append(", (").append(lit.getIssue()).append(")");
        }
        if (lit.getPages() != null && !lit.getPages().isBlank()) {
            sb.append(": ").append(lit.getPages().trim());
        }
        sb.append(".");
        return sb.toString();
    }

    private String formatApa(Literature lit) {
        StringBuilder sb = new StringBuilder();
        String authors = lit.getAuthors();
        if (authors != null && !authors.isBlank()) {
            sb.append(authors.trim()).append(" ");
        }
        if (lit.getYear() != null) {
            sb.append("(").append(lit.getYear()).append("). ");
        } else {
            sb.append("(n.d.). ");
        }
        sb.append(lit.getTitle() != null ? lit.getTitle().trim() : "Untitled");
        sb.append(". ");
        if (lit.getJournal() != null && !lit.getJournal().isBlank()) {
            sb.append("*").append(lit.getJournal().trim()).append("*");
        }
        boolean hasVolume = lit.getVolume() != null && !lit.getVolume().isBlank();
        boolean hasIssue = lit.getIssue() != null && !lit.getIssue().isBlank();
        if (hasVolume) {
            sb.append(", ").append(lit.getVolume());
            if (hasIssue) {
                sb.append("(").append(lit.getIssue()).append(")");
            }
        } else if (hasIssue) {
            sb.append(", (").append(lit.getIssue()).append(")");
        }
        if (lit.getPages() != null && !lit.getPages().isBlank()) {
            sb.append(", ").append(lit.getPages().trim());
        }
        sb.append(".");
        if (lit.getDoi() != null && !lit.getDoi().isBlank()) {
            sb.append(" https://doi.org/").append(lit.getDoi().trim());
        }
        return sb.toString();
    }

    private String formatMla(Literature lit) {
        StringBuilder sb = new StringBuilder();
        String authors = lit.getAuthors();
        if (authors != null && !authors.isBlank()) {
            sb.append(authors.trim()).append(". ");
        }
        sb.append("\"").append(lit.getTitle() != null ? lit.getTitle().trim() : "Untitled").append(".\" ");
        if (lit.getJournal() != null && !lit.getJournal().isBlank()) {
            sb.append(lit.getJournal().trim()).append(", ");
        }
        if (lit.getVolume() != null && !lit.getVolume().isBlank()) {
            sb.append("vol. ").append(lit.getVolume()).append(", ");
        }
        if (lit.getIssue() != null && !lit.getIssue().isBlank()) {
            sb.append("no. ").append(lit.getIssue()).append(", ");
        }
        if (lit.getYear() != null) {
            sb.append(lit.getYear()).append(", ");
        }
        if (lit.getPages() != null && !lit.getPages().isBlank()) {
            sb.append("pp. ").append(lit.getPages().trim()).append(".");
        } else {
            if (sb.length() >= 2) {
                sb.setLength(sb.length() - 2);
            }
            sb.append(".");
        }
        return sb.toString();
    }

    private String chineseToPinyin(String text) {
        if (text == null) return "";
        String firstChar = text.trim();
        if (firstChar.isEmpty()) return "";
        return String.valueOf(firstChar.charAt(0));
    }
}
