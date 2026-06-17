package com.tcm.platform.service;

import com.tcm.platform.dto.AIAnswerResponse;
import com.tcm.platform.dto.AIQuestionRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AIServiceTest {

    @Test
    void answerRejectsBlankQuestion() {
        AIService service = new AIService(mock(DashScopeClient.class), "");

        assertThatThrownBy(() -> service.answer("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("问题不能为空");
    }

    @Test
    void answerUsesFallbackWhenApiKeyIsMissing() {
        DashScopeClient dashScopeClient = mock(DashScopeClient.class);
        AIService service = new AIService(dashScopeClient, "");

        AIAnswerResponse response = service.answer("春季容易困倦如何调养？");

        assertThat(response.fallback()).isTrue();
        assertThat(response.answer()).contains("规律作息", "及时就医");
        assertThat(response.disclaimer()).contains("不能替代医生");
        verifyNoInteractions(dashScopeClient);
    }

    @Test
    void answerReturnsDashScopeAnswerWhenCallSucceeds() {
        DashScopeClient dashScopeClient = mock(DashScopeClient.class);
        when(dashScopeClient.ask("test-key", "春季容易困倦如何调养？", List.of()))
                .thenReturn("建议早睡早起，并适量运动。");
        AIService service = new AIService(dashScopeClient, "test-key");

        AIAnswerResponse response = service.answer("春季容易困倦如何调养？");

        assertThat(response.answer()).isEqualTo("建议早睡早起，并适量运动。");
        assertThat(response.fallback()).isFalse();
        assertThat(response.disclaimer()).contains("不能替代医生");
    }

    @Test
    void answerPassesRecentContextToDashScope() {
        DashScopeClient dashScopeClient = mock(DashScopeClient.class);
        List<AIQuestionRequest.ContextMessage> context = List.of(
                new AIQuestionRequest.ContextMessage("user", "我最近下午容易疲倦"),
                new AIQuestionRequest.ContextMessage("assistant", "可以先观察作息和饮食")
        );
        when(dashScopeClient.ask("test-key", "那晚饭要注意什么？", context))
                .thenReturn("晚饭建议清淡适量，避免过晚。");
        AIService service = new AIService(dashScopeClient, "test-key");

        AIAnswerResponse response = service.answer("那晚饭要注意什么？", context);

        assertThat(response.answer()).contains("晚饭建议");
        verify(dashScopeClient).ask("test-key", "那晚饭要注意什么？", context);
    }

    @Test
    void answerUsesFallbackWhenDashScopeCallFails() {
        DashScopeClient dashScopeClient = mock(DashScopeClient.class);
        when(dashScopeClient.ask("test-key", "最近胃部不适怎么办？", List.of()))
                .thenThrow(new IllegalStateException("external service unavailable"));
        AIService service = new AIService(dashScopeClient, "test-key");

        AIAnswerResponse response = service.answer("最近胃部不适怎么办？");

        assertThat(response.fallback()).isTrue();
        assertThat(response.answer()).contains("及时就医");
    }
}
