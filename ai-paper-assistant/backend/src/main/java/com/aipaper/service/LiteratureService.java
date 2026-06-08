package com.aipaper.service;

import com.aipaper.dto.LiteratureDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface LiteratureService {

    /**
     * 上传文献PDF
     * @param file    PDF文件
     * @param userId  用户ID
     * @return 文献DTO
     */
    LiteratureDTO upload(MultipartFile file, Long userId) throws Exception;

    /**
     * 获取用户的文献列表
     * @param userId 用户ID
     * @return 文献DTO列表，按创建时间倒序
     */
    List<LiteratureDTO> listByUser(Long userId);

    /**
     * 根据ID获取文献详情
     * @param id     文献ID
     * @param userId 用户ID（归属校验）
     * @return 文献DTO
     */
    LiteratureDTO getById(Long id, Long userId);

    /**
     * 删除文献
     * @param id     文献ID
     * @param userId 用户ID（归属校验）
     */
    void delete(Long id, Long userId);

    /**
     * 生成文献AI摘要
     * @param litId  文献ID
     * @param userId 用户ID（归属校验）
     * @return AI摘要JSON字符串
     */
    String generateSummary(Long litId, Long userId) throws Exception;
}
