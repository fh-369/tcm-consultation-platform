package com.tcm.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 药膳实体
 */
@Data
@TableName("recipes")
public class Recipe {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // TODO: 添加以下字段
    // - name, season (春/夏/秋/冬/四季), constitution
    // - suitableFor, summary, ingredients (JSON), steps (JSON)
    // - imageUrl, published (Boolean), viewCount (Integer)
    // - createdAt (INSERT), updatedAt (INSERT_UPDATE)
    
    
}
