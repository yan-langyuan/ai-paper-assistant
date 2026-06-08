package com.aipaper.service;

import com.aipaper.dto.DetectResult;
import com.aipaper.dto.RewriteResult;

public interface ParaphraseService {

    /**
     * 查重检测：将文本拆分为句子，估算每句相似度
     * @param text   待检测文本
     * @param userId 用户ID（记录用量）
     * @return 检测结果（句子级相似度）
     */
    DetectResult detect(String text, Long userId);

    /**
     * 降重改写：调用AI生成三种改写方案
     * @param sentence 待改写句子
     * @param userId   用户ID（记录用量）
     * @return 三种改写方案
     */
    RewriteResult rewrite(String sentence, Long userId) throws Exception;
}
