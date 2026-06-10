package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.entity.KnowledgeArticle;
import com.tcm.platform.mapper.KnowledgeArticleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 中医知识文章公开查询与后台管理业务。
 */
@Service
public class KnowledgeArticleService {

    private final KnowledgeArticleMapper knowledgeArticleMapper;

    public KnowledgeArticleService(KnowledgeArticleMapper knowledgeArticleMapper) {
        this.knowledgeArticleMapper = knowledgeArticleMapper;
    }

    public List<KnowledgeArticle> listPublishedArticles() {
        return knowledgeArticleMapper.selectList(
                new LambdaQueryWrapper<KnowledgeArticle>()
                        .eq(KnowledgeArticle::getPublished, true)
                        .orderByDesc(KnowledgeArticle::getCreatedAt)
        );
    }

    public KnowledgeArticle getPublishedArticle(Long id) {
        KnowledgeArticle article = getArticle(id);
        if (!Boolean.TRUE.equals(article.getPublished())) {
            throw new IllegalArgumentException("知识文章不存在或尚未发布");
        }
        return article;
    }

    public Page<KnowledgeArticle> listArticles(
            long current,
            long size,
            String category,
            Boolean published,
            String keyword
    ) {
        validatePage(current, size);

        LambdaQueryWrapper<KnowledgeArticle> query = new LambdaQueryWrapper<>();
        query.eq(hasText(category), KnowledgeArticle::getCategory, category)
                .eq(published != null, KnowledgeArticle::getPublished, published)
                .and(hasText(keyword), wrapper -> wrapper
                        .like(KnowledgeArticle::getTitle, keyword)
                        .or()
                        .like(KnowledgeArticle::getSummary, keyword))
                .orderByDesc(KnowledgeArticle::getCreatedAt);

        return knowledgeArticleMapper.selectPage(new Page<>(current, size), query);
    }

    public KnowledgeArticle getArticle(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("知识文章 ID 不能为空");
        }

        KnowledgeArticle article = knowledgeArticleMapper.selectById(id);
        if (article == null) {
            throw new IllegalArgumentException("知识文章不存在");
        }
        return article;
    }

    @Transactional
    public KnowledgeArticle createArticle(KnowledgeArticle article) {
        validateArticle(article);
        article.setId(null);
        article.setPublished(Boolean.TRUE.equals(article.getPublished()));
        article.setViewCount(article.getViewCount() == null ? 0 : article.getViewCount());

        if (knowledgeArticleMapper.insert(article) != 1) {
            throw new IllegalStateException("创建知识文章失败");
        }
        return article;
    }

    @Transactional
    public KnowledgeArticle updateArticle(Long id, KnowledgeArticle request) {
        KnowledgeArticle article = getArticle(id);
        validateArticle(request);

        article.setTitle(request.getTitle());
        article.setCategory(request.getCategory());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setTips(request.getTips());
        article.setCoverImageUrl(request.getCoverImageUrl());
        article.setPublished(Boolean.TRUE.equals(request.getPublished()));

        if (knowledgeArticleMapper.updateById(article) != 1) {
            throw new IllegalStateException("更新知识文章失败");
        }
        return article;
    }

    @Transactional
    public void deleteArticle(Long id) {
        getArticle(id);
        if (knowledgeArticleMapper.deleteById(id) != 1) {
            throw new IllegalStateException("删除知识文章失败");
        }
    }

    private void validateArticle(KnowledgeArticle article) {
        if (article == null) {
            throw new IllegalArgumentException("知识文章内容不能为空");
        }
        if (!hasText(article.getTitle())) {
            throw new IllegalArgumentException("文章标题不能为空");
        }
        if (!hasText(article.getContent())) {
            throw new IllegalArgumentException("文章正文不能为空");
        }
    }

    private void validatePage(long current, long size) {
        if (current < 1) {
            throw new IllegalArgumentException("页码必须大于 0");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("每页数量必须在 1 到 100 之间");
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
