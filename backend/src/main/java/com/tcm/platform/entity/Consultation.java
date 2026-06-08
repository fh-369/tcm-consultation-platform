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
    
    // TODO: 添加以下字段
    // - patientAccountId (Long, FK)
    // - patientName (String, 必填)
    // - age (Integer)
    // - gender (String: 男/女/其他)
    // - phone (String)
    // - symptoms (String, 必填)
    // - duration (String)
    // - allergyHistory (String)
    // - urgency (String: 普通/紧急/非常紧急)
    // - patientNote (String)
    // - reminderLevel (String: normal/attention/urgent)
    // - reminderText (String)
    // - status (String: 待接诊/接诊中/已完成)
    // - doctorNote (String)
    // - doctorId (Long, FK)
    // - followUpAt (LocalDateTime)
    // - createdAt (INSERT), updatedAt (INSERT_UPDATE)
    
    
}
