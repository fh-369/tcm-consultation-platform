package com.tcm.platform.common;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 统一 API 响应封装类
 * 
 * 任务：
 * 1. 使用 Lombok @Data 注解（已添加）
 * 2. 定义以下字段：code(int), message(String), data(T), timestamp(LocalDateTime)
 * 3. 实现 4 个静态工厂方法：
 *    - success(T data)          → code=200, message="success"
 *    - success(String msg, T data) → code=200, 自定义 message
 *    - error(int code, String msg) → 错误响应
 *    - error(String msg)         → code=500
 */
@Data
public class Result<T> {
    
    // TODO: 定义字段 code, message, data, timestamp
    
    
    
    
    
    /**
     * 成功响应（仅数据）
     */
    public static <T> Result<T> success(T data) {
        // TODO: 实现此方法
        // 1. 创建 Result 实例
        // 2. 设置 code = 200
        // 3. 设置 message = "success"
        // 4. 设置 data 和 timestamp
        // 5. 返回 result
        
        return null; // 替换为你的实现
    }
    
    /**
     * 成功响应（自定义消息 + 数据）
     */
    public static <T> Result<T> success(String message, T data) {
        // TODO: 实现此方法（参考上面的 success 方法）
        
        return null; // 替换为你的实现
    }
    
    /**
     * 错误响应（指定错误码和消息）
     */
    public static <T> Result<T> error(int code, String message) {
        // TODO: 实现此方法
        
        return null; // 替换为你的实现
    }
    
    /**
     * 错误响应（默认 500）
     */
    public static <T> Result<T> error(String message) {
        // TODO: 调用上面的 error(code, message) 方法，code 设为 500
        
        return null; // 替换为你的实现
    }
}
