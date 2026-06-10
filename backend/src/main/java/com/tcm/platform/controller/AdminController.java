package com.tcm.platform.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.common.Result;
import com.tcm.platform.dto.ConsultationUpdateRequest;
import com.tcm.platform.entity.Consultation;
import com.tcm.platform.entity.KnowledgeArticle;
import com.tcm.platform.entity.User;
import com.tcm.platform.mapper.UserMapper;
import com.tcm.platform.service.ConsultationService;
import com.tcm.platform.service.KnowledgeArticleService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 医生和管理员查询、处理问诊单的接口。
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ConsultationService consultationService;
    private final KnowledgeArticleService knowledgeArticleService;
    private final UserMapper userMapper;

    public AdminController(
            ConsultationService consultationService,
            KnowledgeArticleService knowledgeArticleService,
            UserMapper userMapper
    ) {
        this.consultationService = consultationService;
        this.knowledgeArticleService = knowledgeArticleService;
        this.userMapper = userMapper;
    }

    @GetMapping("/consultation")
    public Result<Page<Consultation>> listConsultations(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String urgency,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(
                consultationService.listConsultations(current, size, status, urgency, null, keyword)
        );
    }

    @PutMapping("/consultation/{id}")
    public Result<Consultation> updateConsultation(
            @PathVariable Long id,
            Authentication authentication,
            @RequestBody ConsultationUpdateRequest request
    ) {
        request.setDoctorId(currentUser(authentication).getId());
        return Result.success("问诊更新成功", consultationService.updateConsultation(id, request));
    }

    @GetMapping("/knowledge")
    public Result<Page<KnowledgeArticle>> listKnowledgeArticles(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean published,
            @RequestParam(required = false) String keyword
    ) {
        return Result.success(
                knowledgeArticleService.listArticles(current, size, category, published, keyword)
        );
    }

    @GetMapping("/knowledge/{id}")
    public Result<KnowledgeArticle> getKnowledgeArticle(@PathVariable Long id) {
        return Result.success(knowledgeArticleService.getArticle(id));
    }

    @PostMapping("/knowledge")
    public Result<KnowledgeArticle> createKnowledgeArticle(@RequestBody KnowledgeArticle article) {
        return Result.success("知识文章创建成功", knowledgeArticleService.createArticle(article));
    }

    @PutMapping("/knowledge/{id}")
    public Result<KnowledgeArticle> updateKnowledgeArticle(
            @PathVariable Long id,
            @RequestBody KnowledgeArticle article
    ) {
        return Result.success("知识文章更新成功", knowledgeArticleService.updateArticle(id, article));
    }

    @DeleteMapping("/knowledge/{id}")
    public Result<Void> deleteKnowledgeArticle(@PathVariable Long id) {
        knowledgeArticleService.deleteArticle(id);
        return Result.success("知识文章删除成功", null);
    }

    private User currentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("后台用户未登录");
        }

        User user = userMapper.selectOne(
                Wrappers.<User>lambdaQuery().eq(User::getUsername, authentication.getName())
        );
        if (user == null) {
            throw new IllegalArgumentException("当前登录账号不是后台账号");
        }
        return user;
    }
}
