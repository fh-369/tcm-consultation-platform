package com.tcm.platform.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.dto.AIQuestionRequest;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.entity.KnowledgeArticle;
import com.tcm.platform.entity.Recipe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 为 AI 问答组装平台内可参考的知识、药膳和问诊单上下文。
 */
@Service
public class AIContextService {

    private final KnowledgeArticleService knowledgeArticleService;
    private final RecipeService recipeService;
    private final ConsultationService consultationService;

    public AIContextService(
            KnowledgeArticleService knowledgeArticleService,
            RecipeService recipeService,
            ConsultationService consultationService
    ) {
        this.knowledgeArticleService = knowledgeArticleService;
        this.recipeService = recipeService;
        this.consultationService = consultationService;
    }

    public List<AIQuestionRequest.ContextMessage> enrichContext(
            String question,
            List<AIQuestionRequest.ContextMessage> existingContext,
            Long patientAccountId,
            Long consultationId
    ) {
        List<AIQuestionRequest.ContextMessage> result = new ArrayList<>();
        if (existingContext != null) {
            result.addAll(existingContext);
        }

        String reference = buildReference(question, patientAccountId, consultationId);
        if (hasText(reference)) {
            result.add(new AIQuestionRequest.ContextMessage("user", reference));
        }
        return result;
    }

    private String buildReference(String question, Long patientAccountId, Long consultationId) {
        List<String> sections = new ArrayList<>();
        List<KnowledgeArticle> articles = records(knowledgeArticleService.listPublishedArticles(1, 3, null, question));
        List<Recipe> recipes = records(recipeService.listRecipes(1, 3, null, null, true, question));

        if (!articles.isEmpty() || !recipes.isEmpty()) {
            sections.add(buildPlatformReference(articles, recipes));
        }
        if (consultationId != null) {
            sections.add(buildConsultationReference(consultationService.getPatientConsultation(consultationId, patientAccountId)));
        }

        if (sections.isEmpty()) {
            return "";
        }
        return "以下是平台参考资料。请优先结合这些资料回答，保持谨慎，不要诊断或开方：\n\n"
                + String.join("\n\n", sections);
    }

    private String buildPlatformReference(List<KnowledgeArticle> articles, List<Recipe> recipes) {
        StringBuilder builder = new StringBuilder("【平台参考资料】");
        for (KnowledgeArticle article : articles) {
            builder.append("\n- 养生知识《")
                    .append(text(article.getTitle()))
                    .append("》")
                    .append("（")
                    .append(text(article.getCategory()))
                    .append("）：")
                    .append(shorten(firstText(article.getSummary(), article.getContent()), 180));
        }
        for (Recipe recipe : recipes) {
            builder.append("\n- 药膳推荐《")
                    .append(text(recipe.getName()))
                    .append("》")
                    .append("（")
                    .append(text(recipe.getSeason()))
                    .append(" · ")
                    .append(text(recipe.getConstitution()))
                    .append("）：")
                    .append(shorten(firstText(recipe.getSummary(), recipe.getSuitableFor(), recipe.getIngredients()), 180));
        }
        return builder.toString();
    }

    private String buildConsultationReference(Consultation consultation) {
        return """
                【用户选择的问诊单】
                - 患者：%s
                - 主要症状：%s
                - 持续时间：%s
                - 紧急程度：%s
                - 备注：%s
                - 系统提醒：%s
                """.formatted(
                text(consultation.getPatientName()),
                text(consultation.getSymptoms()),
                text(consultation.getDuration()),
                text(consultation.getUrgency()),
                text(consultation.getPatientNote()),
                text(consultation.getReminderText())
        ).trim();
    }

    private <T> List<T> records(Page<T> page) {
        return page == null || page.getRecords() == null ? List.of() : page.getRecords();
    }

    private String firstText(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value.trim();
            }
        }
        return "";
    }

    private String shorten(String value, int maxLength) {
        String text = text(value);
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }

    private String text(Object value) {
        return Objects.toString(value, "").trim();
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
