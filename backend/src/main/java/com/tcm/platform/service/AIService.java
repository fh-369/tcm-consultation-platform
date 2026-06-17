package com.tcm.platform.service;

import com.tcm.platform.dto.AIAnswerResponse;
import com.tcm.platform.dto.AIQuestionRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI 养生问答业务，外部服务不可用时提供保守的 fallback 回答。
 */
@Service
public class AIService {

    private static final Logger log = LoggerFactory.getLogger(AIService.class);

    private static final String DISCLAIMER = "本回答仅供一般养生参考，不能替代医生诊断和治疗。";
    private static final String FALLBACK_ANSWER =
            "暂时无法获取智能回答。建议保持规律作息、均衡饮食和适量运动；"
                    + "如症状严重、持续不缓解或出现明显不适，请及时就医。";

    private final DashScopeClient dashScopeClient;
    private final String apiKey;

    public AIService(
            DashScopeClient dashScopeClient,
            @Value("${ai.dashscope.api-key:}") String apiKey
    ) {
        this.dashScopeClient = dashScopeClient;
        this.apiKey = apiKey;
    }

    public AIAnswerResponse answer(String question) {
        return answer(question, List.of());
    }

    public AIAnswerResponse answer(String question, List<AIQuestionRequest.ContextMessage> context) {
        if (!hasText(question)) {
            throw new IllegalArgumentException("问题不能为空");
        }
        if (!hasText(apiKey)) {
            return fallback();
        }

        try {
            return new AIAnswerResponse(dashScopeClient.ask(apiKey, question.trim(), safeContext(context)), false, DISCLAIMER);
        } catch (RuntimeException ex) {
            log.warn("DashScope AI answer failed: {}", ex.getMessage());
            return fallback();
        }
    }

    private List<AIQuestionRequest.ContextMessage> safeContext(List<AIQuestionRequest.ContextMessage> context) {
        return context == null ? List.of() : context;
    }

    private AIAnswerResponse fallback() {
        return new AIAnswerResponse(FALLBACK_ANSWER, true, DISCLAIMER);
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
