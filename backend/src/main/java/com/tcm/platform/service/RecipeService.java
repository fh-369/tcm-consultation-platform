package com.tcm.platform.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.platform.entity.Recipe;
import com.tcm.platform.mapper.RecipeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 药膳公开查询与后台管理业务。
 */
@Service
public class RecipeService {

    private final RecipeMapper recipeMapper;

    public RecipeService(RecipeMapper recipeMapper) {
        this.recipeMapper = recipeMapper;
    }

    public List<Recipe> listPublishedRecipes() {
        return recipeMapper.selectList(
                new LambdaQueryWrapper<Recipe>()
                        .eq(Recipe::getPublished, true)
                        .orderByDesc(Recipe::getCreatedAt)
        );
    }

    public Recipe getPublishedRecipe(Long id) {
        Recipe recipe = getRecipe(id);
        if (!Boolean.TRUE.equals(recipe.getPublished())) {
            throw new IllegalArgumentException("药膳不存在或尚未发布");
        }
        return recipe;
    }

    public Page<Recipe> listRecipes(
            long current,
            long size,
            String season,
            String constitution,
            Boolean published,
            String keyword
    ) {
        validatePage(current, size);

        LambdaQueryWrapper<Recipe> query = new LambdaQueryWrapper<>();
        query.eq(hasText(season), Recipe::getSeason, season)
                .eq(hasText(constitution), Recipe::getConstitution, constitution)
                .eq(published != null, Recipe::getPublished, published)
                .and(hasText(keyword), wrapper -> wrapper
                        .like(Recipe::getName, keyword)
                        .or()
                        .like(Recipe::getSummary, keyword)
                        .or()
                        .like(Recipe::getSuitableFor, keyword))
                .orderByDesc(Recipe::getCreatedAt);

        return recipeMapper.selectPage(new Page<>(current, size), query);
    }

    public Recipe getRecipe(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("药膳 ID 不能为空");
        }

        Recipe recipe = recipeMapper.selectById(id);
        if (recipe == null) {
            throw new IllegalArgumentException("药膳不存在");
        }
        return recipe;
    }

    @Transactional
    public Recipe createRecipe(Recipe recipe) {
        validateRecipe(recipe);
        recipe.setId(null);
        recipe.setPublished(Boolean.TRUE.equals(recipe.getPublished()));
        recipe.setViewCount(recipe.getViewCount() == null ? 0 : recipe.getViewCount());

        if (recipeMapper.insert(recipe) != 1) {
            throw new IllegalStateException("创建药膳失败");
        }
        return recipe;
    }

    @Transactional
    public Recipe updateRecipe(Long id, Recipe request) {
        Recipe recipe = getRecipe(id);
        validateRecipe(request);

        recipe.setName(request.getName());
        recipe.setSeason(request.getSeason());
        recipe.setConstitution(request.getConstitution());
        recipe.setSuitableFor(request.getSuitableFor());
        recipe.setSummary(request.getSummary());
        recipe.setIngredients(request.getIngredients());
        recipe.setSteps(request.getSteps());
        recipe.setImageUrl(request.getImageUrl());
        recipe.setPublished(Boolean.TRUE.equals(request.getPublished()));

        if (recipeMapper.updateById(recipe) != 1) {
            throw new IllegalStateException("更新药膳失败");
        }
        return recipe;
    }

    @Transactional
    public void deleteRecipe(Long id) {
        getRecipe(id);
        if (recipeMapper.deleteById(id) != 1) {
            throw new IllegalStateException("删除药膳失败");
        }
    }

    private void validateRecipe(Recipe recipe) {
        if (recipe == null) {
            throw new IllegalArgumentException("药膳内容不能为空");
        }
        if (!hasText(recipe.getName())) {
            throw new IllegalArgumentException("药膳名称不能为空");
        }
        if (!hasText(recipe.getIngredients())) {
            throw new IllegalArgumentException("药膳食材不能为空");
        }
        if (!hasText(recipe.getSteps())) {
            throw new IllegalArgumentException("药膳制作步骤不能为空");
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
