package com.aipaper.service.impl;

import com.aipaper.dto.RewriteResult;
import com.aipaper.dto.SchemeOption;
import com.aipaper.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.*;

@Service
public class AiServiceImpl implements AiService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiUrl;
    private final String apiKey;
    private final String apiModel;

    public AiServiceImpl(
            @Value("${ai.api.url}") String apiUrl,
            @Value("${ai.api.key}") String apiKey,
            @Value("${ai.api.model}") String apiModel,
            RestTemplateBuilder restTemplateBuilder) {
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
        this.apiModel = apiModel;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(30))
                .setReadTimeout(Duration.ofSeconds(30))
                .build();
    }

    @Override
    public String generateSummary(String fullText) throws Exception {
        String prompt = "你是一位学术论文分析专家。请分析以下论文全文，并以JSON格式返回结果（不要包含markdown代码块标记，只输出纯JSON）。" +
                "JSON必须包含以下字段：\n" +
                "1. background: 研究背景（100字以内）\n" +
                "2. question: 研究问题/目标（一句话概括）\n" +
                "3. method: 研究方法（简要说明）\n" +
                "4. findings: 主要发现（字符串数组格式，每条发现独立成句，如 [\"发现1\", \"发现2\", \"发现3\"]）\n" +
                "5. limitations: 研究局限（简要说明）\n\n" +
                "示例JSON格式：\n" +
                "{\"background\":\"...\",\"question\":\"...\",\"method\":\"...\",\"findings\":[\"发现一\",\"发现二\",\"发现三\"],\"limitations\":\"...\"}\n\n" +
                "论文全文：\n" + fullText;

        String responseJson = callAiApi(prompt);

        // 清理可能的 markdown 代码块标记
        responseJson = responseJson.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

        // 验证返回内容是否为有效JSON
        JsonNode root = objectMapper.readTree(responseJson);
        if (!root.has("background") || !root.has("question") || !root.has("method")
                || !root.has("findings") || !root.has("limitations")) {
            throw new RuntimeException("AI返回的摘要格式不正确，缺少必要字段");
        }

        // 标准化 findings 字段：如果是字符串，转为单元素数组
        if (root.get("findings").isTextual()) {
            var obj = objectMapper.readValue(responseJson, com.fasterxml.jackson.databind.node.ObjectNode.class);
            var arr = objectMapper.createArrayNode();
            arr.add(root.get("findings").asText());
            obj.set("findings", arr);
            responseJson = objectMapper.writeValueAsString(obj);
        }

        return responseJson;
    }

    @Override
    public RewriteResult generateRewriteOptions(String sentence) throws Exception {
        String prompt = "你是一位学术写作助手。请为以下句子提供3种降重改写方案，以JSON格式返回（不要包含markdown代码块标记）。" +
                "JSON格式必须严格如下：\n" +
                "{\n" +
                "  \"schemeA\": {\"text\": \"保守改写方案（保留原句结构，仅替换近义词）\", \"keptTerms\": [\"保留的关键词1\", \"保留的关键词2\"], \"changes\": \"词级替换，保留原句法结构\", \"warning\": \"AI生成，仅供参考\"},\n" +
                "  \"schemeB\": {\"text\": \"平衡改写方案（调整句式和语序）\", \"keptTerms\": [\"保留的关键词1\", \"保留的关键词2\"], \"changes\": \"句式调整为主，辅以词汇替换\", \"warning\": \"AI生成，仅供参考\"},\n" +
                "  \"schemeC\": {\"text\": \"激进改写方案（彻底重组语义表达）\", \"keptTerms\": [\"保留的核心术语1\", \"保留的核心术语2\"], \"changes\": \"语义重组，句法结构完全改变\", \"warning\": \"AI生成，仅供参考\"}\n" +
                "}\n\n" +
                "原句：\n" + sentence;

        String responseJson = callAiApi(prompt);

        // 清理可能的 markdown 代码块标记
        responseJson = responseJson.replaceAll("```json\\s*", "").replaceAll("```\\s*", "").trim();

        JsonNode root = objectMapper.readTree(responseJson);

        RewriteResult result = new RewriteResult();
        result.setSchemeA(parseSchemeOption(root.get("schemeA")));
        result.setSchemeB(parseSchemeOption(root.get("schemeB")));
        result.setSchemeC(parseSchemeOption(root.get("schemeC")));

        return result;
    }

    private SchemeOption parseSchemeOption(JsonNode node) {
        if (node == null) {
            return null;
        }
        SchemeOption option = new SchemeOption();
        option.setText(node.has("text") ? node.get("text").asText() : null);
        option.setChanges(node.has("changes") ? node.get("changes").asText() : null);
        option.setWarning(node.has("warning") ? node.get("warning").asText() : "AI生成，仅供参考");

        if (node.has("keptTerms") && node.get("keptTerms").isArray()) {
            List<String> keptTerms = new ArrayList<>();
            for (JsonNode term : node.get("keptTerms")) {
                keptTerms.add(term.asText());
            }
            option.setKeptTerms(keptTerms);
        }

        return option;
    }

    private String callAiApi(String prompt) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", apiModel);
        requestBody.put("messages", Collections.singletonList(message));
        requestBody.put("temperature", 0.7);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("AI API调用失败，状态码：" + response.getStatusCode());
            }

            String responseBody = response.getBody();
            JsonNode root = objectMapper.readTree(responseBody);

            // 兼容两种返回格式：choices 或直接返回content
            if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                JsonNode choice = root.get("choices").get(0);
                if (choice.has("message") && choice.get("message").has("content")) {
                    return choice.get("message").get("content").asText();
                }
            }

            // 兜底：尝试直接获取content字段
            if (root.has("content")) {
                return root.get("content").asText();
            }

            throw new RuntimeException("AI API返回格式异常：" + responseBody);

        } catch (Exception e) {
            // 重试一次
            try {
                Thread.sleep(1000);
                ResponseEntity<String> retryResponse = restTemplate.postForEntity(apiUrl, request, String.class);
                if (retryResponse.getStatusCode() == HttpStatus.OK) {
                    String responseBody = retryResponse.getBody();
                    JsonNode root = objectMapper.readTree(responseBody);
                    if (root.has("choices") && root.get("choices").isArray() && root.get("choices").size() > 0) {
                        JsonNode choice = root.get("choices").get(0);
                        if (choice.has("message") && choice.get("message").has("content")) {
                            return choice.get("message").get("content").asText();
                        }
                    }
                }
                throw new RuntimeException("AI API重试后仍失败", e);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("AI API调用被中断", ie);
            }
        }
    }
}
