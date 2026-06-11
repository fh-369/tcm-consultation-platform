package com.tcm.platform.controller;

import com.tcm.platform.common.Result;
import com.tcm.platform.dto.AIAnswerResponse;
import com.tcm.platform.dto.AIQuestionRequest;
import com.tcm.platform.service.AIService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 患者 AI 养生问答接口。
 */
@RestController
@RequestMapping("/api/patient/ai")
public class AIController {

    private final AIService aiService;

    public AIController(AIService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/question")
    public Result<AIAnswerResponse> answer(@Valid @RequestBody AIQuestionRequest request) {
        return Result.success(aiService.answer(request.getQuestion()));
    }
}
