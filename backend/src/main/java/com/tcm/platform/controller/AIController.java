package com.tcm.platform.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tcm.platform.common.Result;
import com.tcm.platform.dto.AIAnswerResponse;
import com.tcm.platform.dto.AIContentRecommendation;
import com.tcm.platform.dto.AIQuestionRequest;
import com.tcm.platform.entity.PatientAccount;
import com.tcm.platform.mapper.PatientAccountMapper;
import com.tcm.platform.service.AIService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 患者 AI 养生问答接口。
 */
@RestController
@RequestMapping("/api/patient/ai")
public class AIController {

    private final AIService aiService;
    private final PatientAccountMapper patientAccountMapper;

    public AIController(AIService aiService, PatientAccountMapper patientAccountMapper) {
        this.aiService = aiService;
        this.patientAccountMapper = patientAccountMapper;
    }

    @PostMapping("/question")
    public Result<AIAnswerResponse> answer(Authentication authentication, @Valid @RequestBody AIQuestionRequest request) {
        PatientAccount patient = currentPatient(authentication);
        return Result.success(aiService.answer(
                request.getQuestion(),
                request.getContext(),
                patient.getId(),
                request.getConsultationId()
        ));
    }

    @PostMapping(value = "/question/stream", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<StreamingResponseBody> streamAnswer(
            Authentication authentication,
            @Valid @RequestBody AIQuestionRequest request
    ) {
        PatientAccount patient = currentPatient(authentication);
        StreamingResponseBody body = outputStream -> aiService.streamAnswer(
                request.getQuestion(),
                request.getContext(),
                patient.getId(),
                request.getConsultationId(),
                chunk -> {
                    try {
                        outputStream.write(chunk.getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                    } catch (java.io.IOException ex) {
                        throw new IllegalStateException("AI 流式响应写入失败", ex);
                    }
                }
        );
        return ResponseEntity.ok()
                .contentType(new MediaType("text", "plain", StandardCharsets.UTF_8))
                .body(body);
    }

    @PostMapping("/recommendations")
    public Result<List<AIContentRecommendation>> recommendations(
            @Valid @RequestBody AIQuestionRequest request
    ) {
        return Result.success(aiService.findRecommendations(request.getQuestion()));
    }

    private PatientAccount currentPatient(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("患者未登录");
        }

        PatientAccount patient = patientAccountMapper.selectOne(
                Wrappers.<PatientAccount>lambdaQuery()
                        .eq(PatientAccount::getUsername, authentication.getName())
        );
        if (patient == null) {
            throw new IllegalArgumentException("当前登录账号不是患者账号");
        }
        return patient;
    }
}
