package com.tcm.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 患者账号实体
 * 任务：完善字段定义，对应数据库 patient_accounts 表
 */
@Data
@TableName("patient_accounts")
public class PatientAccount {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // TODO: 添加以下字段
    // - username, passwordHash, displayName, phone, avatarUrl
    // - createdAt (INSERT), updatedAt (INSERT_UPDATE)
    
    
}
