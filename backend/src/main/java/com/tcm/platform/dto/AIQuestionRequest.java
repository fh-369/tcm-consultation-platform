package com.tcm.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 提问请求 DTO
 */
@Data
public class AIQuestionRequest {
    
    // TODO: 添加 @NotBlank(message = "问题不能为空")
    private String question;
}
