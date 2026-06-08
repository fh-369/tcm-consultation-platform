package com.tcm.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文件上传记录实体
 */
@Data
@TableName("uploads")
public class Upload {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // TODO: 添加以下字段
    // - originalName, storedName, mimeType, fileSize
    // - accessUrl, uploadedBy
    // - createdAt (INSERT)
    
    
}
