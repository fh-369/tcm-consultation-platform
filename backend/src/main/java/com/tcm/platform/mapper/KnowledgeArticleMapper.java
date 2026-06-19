package com.tcm.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcm.platform.entity.KnowledgeArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface KnowledgeArticleMapper extends BaseMapper<KnowledgeArticle> {

    @Update("""
            UPDATE knowledge_articles
            SET view_count = COALESCE(view_count, 0) + 1
            WHERE id = #{id} AND published = 1
            """)
    int incrementPublishedViewCount(@Param("id") Long id);
}
