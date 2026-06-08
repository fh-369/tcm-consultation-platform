package com.tcm.platform.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 配置
 * 
 * 任务：
 * 1. 配置分页插件 PaginationInnerInterceptor
 * 2. 配置自动填充处理器 MetaObjectHandler（createdAt / updatedAt）
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // TODO: 创建 MybatisPlusInterceptor
        // 添加 PaginationInnerInterceptor(DbType.MYSQL)
        // 返回 interceptor
        
        return null;
    }

    /**
     * 自动填充处理器 - 自动填充创建时间和更新时间
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        // TODO: 返回 new MetaObjectHandler() 匿名实现
        // insertFill: 填充 createdAt 和 updatedAt 为 LocalDateTime.now()
        // updateFill: 填充 updatedAt 为 LocalDateTime.now()
        // 提示：使用 this.strictInsertFill(metaObject, "fieldName", LocalDateTime.class, value)
        
        return null;
    }
}
