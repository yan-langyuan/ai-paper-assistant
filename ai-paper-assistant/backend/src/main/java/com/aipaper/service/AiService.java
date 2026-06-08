package com.aipaper.service;

import com.aipaper.dto.RewriteResult;

public interface AiService {

    /**
     * 调用AI大模型生成论文摘要
     * @param fullText 论文全文文本
     * @return 包含背景/问题/方法/发现/局限的JSON字符串
     */
    String generateSummary(String fullText) throws Exception;

    /**
     * 调用AI大模型生成句子级降重改写方案
     * @param sentence 待改写句子
     * @return 三种改写方案（保守/平衡/激进）
     */
    RewriteResult generateRewriteOptions(String sentence) throws Exception;
}
