package com.tcm.platform.service;

import com.tcm.platform.dto.AIQuestionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 阿里云 DashScope 文本生成 API 客户端。
 */
@Component
public class DashScopeClient {

    private static final String PROMPT_TEMPLATE = """
            你是一名中医养生助手。请仅提供一般性的生活调养建议，不要进行诊断、开具处方或替代医生。
            回答应简洁、谨慎，并提醒用户：症状严重、持续或出现危险信号时应及时就医。

            %s

            用户当前问题：%s
            """;

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String model;

    public DashScopeClient(
            RestTemplate restTemplate,
            @Value("${ai.dashscope.base-url}") String baseUrl,
            @Value("${ai.dashscope.model}") String model
    ) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.model = model;
    }

    public String ask(String apiKey, String question, List<AIQuestionRequest.ContextMessage> context) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        DashScopeRequest request = new DashScopeRequest(
                model,
                new Input(PROMPT_TEMPLATE.formatted(formatContext(context), question)),
                new Parameters("text")
        );
        DashScopeResponse response = restTemplate.postForObject(
                baseUrl,
                new HttpEntity<>(request, headers),
                DashScopeResponse.class
        );

        if (response == null || response.output() == null || !hasText(response.output().text())) {
            throw new IllegalStateException("DashScope 未返回有效回答");
        }
        return response.output().text().trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private String formatContext(List<AIQuestionRequest.ContextMessage> context) {
        if (context == null || context.isEmpty()) {
            return "最近对话上下文：无";
        }

        StringBuilder builder = new StringBuilder("最近对话上下文：\n");
        context.stream()
                .filter(message -> message != null && hasText(message.content()))
                .filter(message -> "user".equals(message.role()) || "assistant".equals(message.role()))
                .forEach(message -> builder
                        .append("user".equals(message.role()) ? "用户：" : "助手：")
                        .append(message.content().trim())
                        .append('\n'));

        return builder.toString().trim();
    }

    private record DashScopeRequest(String model, Input input, Parameters parameters) {
    }

    private record Input(String prompt) {
    }

    private record Parameters(String result_format) {
    }

    private record DashScopeResponse(Output output) {
    }

    private record Output(String text) {
    }
}
