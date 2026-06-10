package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.entity.KnowledgeArticle;
import com.tcm.platform.mapper.KnowledgeArticleMapper;
import org.apache.ibatis.builder.MapperBuilderAssistant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KnowledgeArticleServiceTest {

    @BeforeAll
    static void initializeTableInfo() {
        TableInfoHelper.initTableInfo(
                new MapperBuilderAssistant(new MybatisConfiguration(), "test"),
                KnowledgeArticle.class
        );
    }

    @Mock
    private KnowledgeArticleMapper knowledgeArticleMapper;

    @Test
    void listPublishedArticlesOnlyQueriesPublishedContent() {
        KnowledgeArticleService service = new KnowledgeArticleService(knowledgeArticleMapper);
        when(knowledgeArticleMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(java.util.List.of());

        service.listPublishedArticles();

        ArgumentCaptor<LambdaQueryWrapper<KnowledgeArticle>> queryCaptor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(knowledgeArticleMapper).selectList(queryCaptor.capture());
        assertThat(queryCaptor.getValue().getCustomSqlSegment()).contains("published");
    }

    @Test
    void createArticleDefaultsToDraftWithZeroViews() {
        KnowledgeArticleService service = new KnowledgeArticleService(knowledgeArticleMapper);
        when(knowledgeArticleMapper.insert(any(KnowledgeArticle.class))).thenReturn(1);
        KnowledgeArticle article = article("春季养生", "春季养生正文");

        KnowledgeArticle created = service.createArticle(article);

        assertThat(created.getPublished()).isFalse();
        assertThat(created.getViewCount()).isZero();
    }

    @Test
    void listArticlesFiltersByCategoryPublishedAndKeyword() {
        KnowledgeArticleService service = new KnowledgeArticleService(knowledgeArticleMapper);
        when(knowledgeArticleMapper.selectPage(any(IPage.class), any(LambdaQueryWrapper.class)))
                .thenReturn(new Page<>());

        service.listArticles(1, 10, "四季养生", true, "春季");

        ArgumentCaptor<LambdaQueryWrapper<KnowledgeArticle>> queryCaptor =
                ArgumentCaptor.forClass(LambdaQueryWrapper.class);
        verify(knowledgeArticleMapper).selectPage(any(IPage.class), queryCaptor.capture());
        assertThat(queryCaptor.getValue().getCustomSqlSegment())
                .contains("category", "published", "title", "summary", "OR");
    }

    @Test
    void deleteArticleDeletesExistingArticle() {
        KnowledgeArticleService service = new KnowledgeArticleService(knowledgeArticleMapper);
        when(knowledgeArticleMapper.selectById(9L)).thenReturn(article("待删除文章", "待删除正文"));
        when(knowledgeArticleMapper.deleteById(9L)).thenReturn(1);

        service.deleteArticle(9L);

        verify(knowledgeArticleMapper).deleteById(9L);
    }

    private KnowledgeArticle article(String title, String content) {
        KnowledgeArticle article = new KnowledgeArticle();
        article.setTitle(title);
        article.setContent(content);
        return article;
    }
}
