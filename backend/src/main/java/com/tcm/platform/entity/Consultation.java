package com.tcm.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 问诊单实体（核心业务表）
 * 任务：完善字段定义，对应数据库 consultations 表
 */
@Data
@TableName("consultations")
public class Consultation {
    
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long patientAccountId;

    private String patientName;

    private Integer age;

    private String gender;

    private String phone;

    private String symptoms;

    private String duration;

    private String allergyHistory;

    private String urgency;

    private String patientNote;

    private String reminderLevel;

    private String reminderText;

    private String status;

    private String doctorNote;

    private Long doctorId;

    private LocalDateTime followUpAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
