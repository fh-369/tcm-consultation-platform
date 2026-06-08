package com.tcm.platform.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 问诊更新请求 DTO
 */
@Data
public class ConsultationUpdateRequest {
    private String status;
    private String doctorNote;
    private Long doctorId;
    private LocalDateTime followUpAt;
}
