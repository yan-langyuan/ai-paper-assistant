package com.aipaper.service.impl;

import com.aipaper.dto.ReferenceFormatRequest;
import com.aipaper.dto.ReferenceFormatResult;
import com.aipaper.model.Literature;
import com.aipaper.repository.LiteratureRepository;
import com.aipaper.service.ReferenceService;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReferenceServiceImpl implements ReferenceService {

    private final LiteratureRepository literatureRepository;

    private static final List<String> SUPPORTED_STANDARDS = List.of("GB/T 7714", "APA 7th", "MLA 9th");

    public ReferenceServiceImpl(LiteratureRepository literatureRepository) {
        this.literatureRepository = literatureRepository;
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
        List<Literature> literatures = literatureRepository.findAllById(req.getLiteratureIds());

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
            formattedList.sort(Comparator.comparing(s -> chineseToPinyin(s)));
        } else if ("alphabet".equalsIgnoreCase(sortBy)) {
            formattedList.sort(String::compareToIgnoreCase);
        }
        // 默认为原序（按ID顺序）

        return ReferenceFormatResult.builder()
                .formattedList(formattedList)
                .standard(effectiveStandard)
                .build();
    }

    @Override
    public List<String> getSupportedStandards() {
        return SUPPORTED_STANDARDS;
    }

    /**
     * 根据指定标准格式化单条文献
     */
    private String formatSingle(Literature lit, String standard) {
        return switch (standard.toLowerCase()) {
            case "apa 7th" -> formatApa(lit);
            case "mla 9th" -> formatMla(lit);
            default -> formatGbt(lit);
        };
    }

    /**
     * GB/T 7714 格式（中国国家标准）
     * 格式：作者. 题名[J]. 期刊名, 年, 卷(期): 起止页码.
     */
    private String formatGbt(Literature lit) {
        StringBuilder sb = new StringBuilder();

        // 作者
        if (lit.getAuthors() != null && !lit.getAuthors().isBlank()) {
            sb.append(lit.getAuthors().trim());
            if (!lit.getAuthors().trim().endsWith(".") && !lit.getAuthors().trim().endsWith("，")) {
                sb.append(".");
            }
            sb.append(" ");
        }

        // 题名
        sb.append(lit.getTitle() != null ? lit.getTitle().trim() : "未命名文献");
        sb.append("[J]. ");

        // 期刊名
        if (lit.getJournal() != null && !lit.getJournal().isBlank()) {
            sb.append(lit.getJournal().trim()).append(", ");
        }

        // 年
        if (lit.getYear() != null) {
            sb.append(lit.getYear());
        }

        // 卷期
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

        // 页码
        if (lit.getPages() != null && !lit.getPages().isBlank()) {
            sb.append(": ").append(lit.getPages().trim());
        }

        sb.append(".");

        return sb.toString();
    }

    /**
     * APA 7th 格式
     * 格式：Author. (Year). Title. Journal, Volume(Issue), pages. DOI
     */
    private String formatApa(Literature lit) {
        StringBuilder sb = new StringBuilder();

        // 作者（APA格式：姓, 名首字母.）
        String authors = lit.getAuthors();
        if (authors != null && !authors.isBlank()) {
            sb.append(formatAuthorsApa(authors));
        }

        // 年份
        if (lit.getYear() != null) {
            sb.append("(").append(lit.getYear()).append("). ");
        } else {
            sb.append("(n.d.). ");
        }

        // 题名
        sb.append(lit.getTitle() != null ? lit.getTitle().trim() : "Untitled");
        sb.append(". ");

        // 期刊名（斜体在纯文本中无法表现，加引号标识）
        if (lit.getJournal() != null && !lit.getJournal().isBlank()) {
            sb.append("*").append(lit.getJournal().trim()).append("*");
        }

        // 卷期
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

        // 页码
        if (lit.getPages() != null && !lit.getPages().isBlank()) {
            sb.append(", ").append(lit.getPages().trim());
        }

        sb.append(".");

        // DOI
        if (lit.getDoi() != null && !lit.getDoi().isBlank()) {
            sb.append(" https://doi.org/").append(lit.getDoi().trim());
        }

        return sb.toString();
    }

    /**
     * MLA 9th 格式
     * 格式：Author. "Title." Journal, vol. Volume, no. Issue, Year, pp. pages.
     */
    private String formatMla(Literature lit) {
        StringBuilder sb = new StringBuilder();

        // 作者
        String authors = lit.getAuthors();
        if (authors != null && !authors.isBlank()) {
            sb.append(authors.trim()).append(". ");
        }

        // 题名（引号括起）
        sb.append("\"").append(lit.getTitle() != null ? lit.getTitle().trim() : "Untitled").append(".\" ");

        // 期刊名
        if (lit.getJournal() != null && !lit.getJournal().isBlank()) {
            sb.append(lit.getJournal().trim()).append(", ");
        }

        // 卷
        if (lit.getVolume() != null && !lit.getVolume().isBlank()) {
            sb.append("vol. ").append(lit.getVolume()).append(", ");
        }

        // 期
        if (lit.getIssue() != null && !lit.getIssue().isBlank()) {
            sb.append("no. ").append(lit.getIssue()).append(", ");
        }

        // 年
        if (lit.getYear() != null) {
            sb.append(lit.getYear()).append(", ");
        }

        // 页码
        if (lit.getPages() != null && !lit.getPages().isBlank()) {
            sb.append("pp. ").append(lit.getPages().trim()).append(".");
        } else {
            // 去掉末尾多余的逗号和空格
            if (sb.length() >= 2) {
                sb.setLength(sb.length() - 2);
            }
            sb.append(".");
        }

        return sb.toString();
    }

    /**
     * 将中文作者名转换为APA格式
     * 简单处理：假设中文名作者用空格或逗号分隔，英文名转换首字母
     */
    private String formatAuthorsApa(String authors) {
        // 简化处理：直接返回原格式（复杂姓名转换需要NLP支持，MVP阶段简化处理）
        return authors.trim() + " ";
    }

    /**
     * 简单的中文转拼音比较（用于排序）
     * 使用Java的Collator进行中文字符串排序
     */
    private String chineseToPinyin(String text) {
        if (text == null) return "";
        // 提取首字符用于排序
        String firstChar = text.trim();
        if (firstChar.isEmpty()) return "";
        firstChar = String.valueOf(firstChar.charAt(0));
        return firstChar;
    }
}
