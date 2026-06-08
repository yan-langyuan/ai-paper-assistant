package com.aipaper.service.impl;

import com.aipaper.dto.*;
import com.aipaper.model.AiUsageLog;
import com.aipaper.repository.AiUsageLogRepository;
import com.aipaper.service.AiService;
import com.aipaper.service.ParaphraseService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ParaphraseServiceImpl implements ParaphraseService {

    private final AiService aiService;
    private final AiUsageLogRepository aiUsageLogRepository;

    /**
     * 常见中文标点符号（用于分句）
     */
    private static final String SENTENCE_DELIMITERS = "(?<=[。！？!?\\n])\\s*";

    public ParaphraseServiceImpl(AiService aiService,
                                  AiUsageLogRepository aiUsageLogRepository) {
        this.aiService = aiService;
        this.aiUsageLogRepository = aiUsageLogRepository;
    }

    @Override
    public DetectResult detect(String text, Long userId) {
        // 记录AI用量
        recordUsage(userId, "DETECT", text.length());

        // 分句
        String[] rawSentences = text.split(SENTENCE_DELIMITERS);
        List<String> sentences = Arrays.stream(rawSentences)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        // 内置关键词库（典型学术高频词）
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
        // 记录AI用量
        recordUsage(userId, "REWRITE", sentence.length());

        return aiService.generateRewriteOptions(sentence);
    }

    /**
     * 记录AI使用日志
     */
    private void recordUsage(Long userId, String actionType, int charCount) {
        AiUsageLog log = AiUsageLog.builder()
                .userId(userId)
                .actionType(actionType)
                .charCount(charCount)
                .build();
        aiUsageLogRepository.save(log);
    }

    /**
     * 基于关键词和句长估算相似度（MVP简化实现）
     *
     * 策略：
     * 1. 学术关键词密度越高，相似度越高
     * 2. 句子越长越复杂，相似度越高（长句更难改写）
     * 3. 加入随机抖动模拟真实检测效果
     */
    private double estimateSimilarity(String sentence, Set<String> academicKeywords) {
        // 计算关键词密度
        int keywordCount = 0;
        String[] words = sentence.split("[\\s,，;；、]+");
        for (String word : words) {
            if (academicKeywords.contains(word.trim())) {
                keywordCount++;
            }
        }

        double keywordDensity = words.length > 0 ? (double) keywordCount / words.length : 0;

        // 句长因子（长句倾向高相似度）
        double lengthFactor = Math.min(1.0, sentence.length() / 200.0);

        // 基础相似度 = 关键词密度贡献 + 句长贡献
        double baseSimilarity = keywordDensity * 0.6 + lengthFactor * 0.3;

        // 随机抖动 +/-10%
        Random random = new Random(sentence.hashCode());
        double jitter = (random.nextDouble() - 0.5) * 0.2;

        double similarity = Math.max(0.2, Math.min(0.95, baseSimilarity + jitter));

        return Math.round(similarity * 100.0) / 100.0;
    }

    /**
     * 根据相似度生成建议
     */
    private String generateSuggestion(double similarity) {
        if (similarity >= 0.8) {
            return "建议进行降重改写，相似度较高";
        } else if (similarity >= 0.5) {
            return "相似度中等，可选择性改写";
        } else {
            return "相似度较低，无需改写";
        }
    }

    /**
     * 构建学术关键词库（常见中文论文高频词汇）
     */
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
