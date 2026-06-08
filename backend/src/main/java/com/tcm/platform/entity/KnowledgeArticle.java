package com.tcm.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 中医常识文章实体
 */
@Data
@TableName("knowledge_articles")
public class KnowledgeArticle {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    // TODO: 添加以下字段
    // - title, category, summary, content, tips
    // - coverImageUrl, published (Boolean), viewCount (Integer)
    // - createdAt (INSERT), updatedAt (INSERT_UPDATE)
    
    
}
