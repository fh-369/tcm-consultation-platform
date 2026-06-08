package com.tcm.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcm.platform.entity.Recipe;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface RecipeMapper extends BaseMapper<Recipe> {
}
