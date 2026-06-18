package com.tcm.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.dto.AIQuestionRequest;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.entity.KnowledgeArticle;
import com.tcm.platform.entity.Recipe;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AIContextServiceTest {

    @Test
    void keepsPlatformRecommendationsSeparateFromSelectedConsultationContext() {
        KnowledgeArticleService knowledgeArticleService = mock(KnowledgeArticleService.class);
        RecipeService recipeService = mock(RecipeService.class);
        ConsultationService consultationService = mock(ConsultationService.class);
        AIContextService service = new AIContextService(
                knowledgeArticleService,
                recipeService,
                consultationService
        );
        KnowledgeArticle article = new KnowledgeArticle();
        article.setTitle("晚餐如何吃得更均衡");
        article.setCategory("饮食调养");
        article.setSummary("晚餐建议清淡适量，注意食物多样。");
        Recipe recipe = new Recipe();
        recipe.setName("山药香菇鸡肉粥");
        recipe.setSeason("冬");
        recipe.setConstitution("气虚质");
        recipe.setSummary("口味清淡，适合作为寒冷天气里的日常一餐。");
        Consultation consultation = new Consultation();
        consultation.setPatientName("小舞");
        consultation.setSymptoms("最近胃口不好");
        consultation.setDuration("三天");
        consultation.setUrgency("普通");
        consultation.setPatientNote("饭后有些反胃");
        consultation.setReminderText("建议保持观察。");
        when(knowledgeArticleService.listPublishedArticles(1, 100, null, null))
                .thenReturn(pageOf(article));
        when(recipeService.listRecipes(1, 100, null, null, true, null))
                .thenReturn(pageOf(recipe));
        when(consultationService.getPatientConsultation(12L, 7L)).thenReturn(consultation);
        List<AIQuestionRequest.ContextMessage> existing = List.of(
                new AIQuestionRequest.ContextMessage("user", "我最近吃得少")
        );

        List<AIQuestionRequest.ContextMessage> result = service.enrichContext(
                "最近胃口不好怎么调养？",
                existing,
                7L,
                12L
        );

        assertThat(result).hasSize(2);
        assertThat(result.get(0).content()).isEqualTo("我最近吃得少");
        assertThat(result.get(1).role()).isEqualTo("user");
        assertThat(result.get(1).content())
                .contains("用户选择的问诊单")
                .contains("最近胃口不好")
                .contains("饭后有些反胃")
                .doesNotContain("晚餐如何吃得更均衡")
                .doesNotContain("山药香菇鸡肉粥");

        var recommendations = service.findRecommendations("最近胃口不好怎么调养？");

        assertThat(recommendations).hasSize(2);
        assertThat(recommendations.get(0).type()).isEqualTo("knowledge");
        assertThat(recommendations.get(0).title()).isEqualTo("晚餐如何吃得更均衡");
        assertThat(recommendations.get(1).type()).isEqualTo("recipe");
        assertThat(recommendations.get(1).title()).isEqualTo("山药香菇鸡肉粥");
        verify(consultationService).getPatientConsultation(12L, 7L);
    }

    @SafeVarargs
    private static <T> Page<T> pageOf(T... records) {
        Page<T> page = new Page<>(1, 3);
        page.setRecords(List.of(records));
        return page;
    }
}
