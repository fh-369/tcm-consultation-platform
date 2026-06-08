package com.tcm.platform.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 问诊请求 DTO
 */
@Data
public class ConsultationRequest {
    
    private Long patientAccountId;
    
    // TODO: 添加 @NotBlank(message = "患者姓名不能为空")
    private String patientName;
    
    private Integer age;
    private String gender;
    private String phone;
    
    // TODO: 添加 @NotBlank(message = "症状描述不能为空")
    private String symptoms;
    
    private String duration;
    private String allergyHistory;
    private String urgency;
    private String patientNote;
}
