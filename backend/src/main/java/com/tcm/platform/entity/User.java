package com.tcm.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户实体（医生/管理员）
 * 任务：完善字段定义，对应数据库 users 表
 */
@Data
@TableName("users")
public class User {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // TODO: 添加以下字段（参考数据库表结构）
    // - username (VARCHAR 50, 唯一)
    // - passwordHash (VARCHAR 255, BCrypt 加密)
    // - role (ENUM: admin/doctor)
    // - displayName (VARCHAR 100)
    // - department (VARCHAR 100)
    // - createdAt (DATETIME, 自动填充 INSERT)
    // - updatedAt (DATETIME, 自动填充 INSERT_UPDATE)
    
    
}
