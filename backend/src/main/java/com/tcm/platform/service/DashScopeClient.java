package com.tcm.platform.service;

import com.tcm.platform.dto.AIQuestionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * 阿里云 DashScope OpenAI 兼容模式客户端。
 */
@Component
public class DashScopeClient {

    private static final String SYSTEM_PROMPT = """
            你是一名中医养生助手。请仅提供一般性的生活调养建议，不要进行诊断、开具处方或替代医生。
            回答应简洁、谨慎，并提醒用户：症状严重、持续或出现危险信号时应及时就医。
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
                buildMessages(question, context)
        );
        DashScopeResponse response;
        try {
            response = restTemplate.postForObject(
                    baseUrl,
                    new HttpEntity<>(request, headers),
                    DashScopeResponse.class
            );
        } catch (RestClientResponseException ex) {
            throw new IllegalStateException(
                    "DashScope 调用失败: HTTP " + ex.getRawStatusCode() + " " + ex.getResponseBodyAsString(),
                    ex
            );
        }

        String answer = extractAnswer(response);
        if (!hasText(answer)) {
            throw new IllegalStateException("DashScope 未返回有效回答");
        }
        return answer.trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private List<Message> buildMessages(String question, List<AIQuestionRequest.ContextMessage> context) {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message("system", SYSTEM_PROMPT.trim()));

        if (context != null) {
            context.stream()
                    .filter(message -> message != null && hasText(message.content()))
                    .filter(message -> "user".equals(message.role()) || "assistant".equals(message.role()))
                    .map(message -> new Message(message.role(), message.content().trim()))
                    .forEach(messages::add);
        }

        messages.add(new Message("user", question));
        return messages;
    }

    private String extractAnswer(DashScopeResponse response) {
        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return null;
        }
        Choice firstChoice = response.choices().get(0);
        if (firstChoice == null || firstChoice.message() == null) {
            return null;
        }
        return firstChoice.message().content();
    }

    private record DashScopeRequest(String model, List<Message> messages) {
    }

    private record Message(String role, String content) {
    }

    private record DashScopeResponse(List<Choice> choices) {
    }

    private record Choice(Message message) {
    }
}
