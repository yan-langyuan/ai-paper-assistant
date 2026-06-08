package com.aipaper.service;

import com.aipaper.dto.ReferenceFormatRequest;
import com.aipaper.dto.ReferenceFormatResult;

import java.util.List;

public interface ReferenceService {

    /**
     * 根据指定标准格式化参考文献列表
     * @param req    格式化请求（文献ID列表、标准、排序方式）
     * @param userId 用户ID
     * @return 格式化结果
     */
    ReferenceFormatResult format(ReferenceFormatRequest req, Long userId);

    /**
     * 获取支持的参考文献格式标准列表
     * @return 标准名称列表（如 GB/T 7714, APA 7th, MLA 9th）
     */
    List<String> getSupportedStandards();
}
