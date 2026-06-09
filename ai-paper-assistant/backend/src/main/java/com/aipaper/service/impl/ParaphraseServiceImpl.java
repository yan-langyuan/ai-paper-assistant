package com.aipaper.service.impl;

import com.aipaper.dto.*;
import com.aipaper.mapper.AiUsageLogMapper;
import com.aipaper.model.AiUsageLog;
import com.aipaper.service.AiService;
import com.aipaper.service.ParaphraseService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParaphraseServiceImpl implements ParaphraseService {

    private final AiService aiService;
    private final AiUsageLogMapper aiUsageLogMapper;

    private static final String SENTENCE_DELIMITERS = "(?<=[。！？!?\\n])\\s*";

    public ParaphraseServiceImpl(AiService aiService,
                                  AiUsageLogMapper aiUsageLogMapper) {
        this.aiService = aiService;
        this.aiUsageLogMapper = aiUsageLogMapper;
    }

    @Override
    public DetectResult detect(String text, Long userId) {
        recordUsage(userId, "DETECT", text.length());

        String[] rawSentences = text.split(SENTENCE_DELIMITERS);
        List<String> sentences = Arrays.stream(rawSentences)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        Set<String> academicKeywords = buildAcademicKeywordSet();

        List<SentenceResult> results = new ArrayList<>();
        for (String sentence : sentences) {
            double similarity = estimateSimilarity(sentence, academicKeywords);
            String suggestion = generateSuggestion(similarity);
            results.add(SentenceResult.builder()
                    .text(sentence)
                    .similarity(similarity)
                    .suggestion(suggestion)
                    .build());
        }

        return DetectResult.builder()
                .sentences(results)
                .build();
    }

    @Override
    public RewriteResult rewrite(String sentence, Long userId) throws Exception {
        recordUsage(userId, "REWRITE", sentence.length());
        return aiService.generateRewriteOptions(sentence);
    }

    private void recordUsage(Long userId, String actionType, int charCount) {
        AiUsageLog log = AiUsageLog.builder()
                .userId(userId)
                .actionType(actionType)
                .charCount(charCount)
                .build();
        aiUsageLogMapper.insert(log);
    }

    private double estimateSimilarity(String sentence, Set<String> academicKeywords) {
        int keywordCount = 0;
        String[] words = sentence.split("[\\s,，;；、]+");
        for (String word : words) {
            if (academicKeywords.contains(word.trim())) {
                keywordCount++;
            }
        }
        double keywordDensity = words.length > 0 ? (double) keywordCount / words.length : 0;
        double lengthFactor = Math.min(1.0, sentence.length() / 200.0);
        double baseSimilarity = keywordDensity * 0.6 + lengthFactor * 0.3;

        Random random = new Random(sentence.hashCode());
        double jitter = (random.nextDouble() - 0.5) * 0.2;
        double similarity = Math.max(0.2, Math.min(0.95, baseSimilarity + jitter));
        return Math.round(similarity * 100.0) / 100.0;
    }

    private String generateSuggestion(double similarity) {
        if (similarity >= 0.8) {
            return "建议进行降重改写，相似度较高";
        } else if (similarity >= 0.5) {
            return "相似度中等，可选择性改写";
        } else {
            return "相似度较低，无需改写";
        }
    }

    private Set<String> buildAcademicKeywordSet() {
        Set<String> keywords = new HashSet<>();
        keywords.addAll(List.of(
                "研究", "分析", "方法", "实验", "数据", "结果", "讨论",
                "模型", "系统", "算法", "理论", "问题", "方案", "策略",
                "基于", "采用", "提出", "实现", "验证", "评估", "优化",
                "对比", "性能", "效率", "精度", "特征", "参数", "结构",
                "机制", "影响", "因素", "过程", "功能", "应用", "技术",
                "论文", "本文", "文献", "综述", "调研", "总结", "定义",
                "假设", "条件", "结论", "贡献", "创新", "意义", "目的",
                "对象", "范围", "框架", "平台", "工具", "环境", "配置",
                "实验", "测试", "仿真", "模拟", "采集", "提取", "处理",
                "计算", "推理", "分类", "聚类", "回归", "预测", "拟合",
                "网络", "深度", "学习", "训练", "样本", "特征", "维度"
        ));
        return keywords;
    }
}
