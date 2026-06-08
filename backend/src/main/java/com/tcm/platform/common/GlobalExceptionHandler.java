package com.tcm.platform.common;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 
 * 任务：
 * 1. 添加 @RestControllerAdvice 注解（已添加）
 * 2. 实现 3 个异常处理方法，分别处理不同类型的异常
 * 3. 每个方法返回统一的 Result 响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     * 当 @Valid 校验失败时触发
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleValidationException(MethodArgumentNotValidException ex) {
        // TODO: 从 ex.getBindingResult().getFieldErrors() 中提取错误消息
        // 提示：使用 stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "))
        // 然后返回 Result.error(400, message)
        
        return null; // 替换为你的实现
    }
    
    /**
     * 处理非法参数异常
     * 当业务逻辑抛出 IllegalArgumentException 时触发
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleIllegalArgument(IllegalArgumentException ex) {
        // TODO: 返回 Result.error(400, ex.getMessage())
        
        return null; // 替换为你的实现
    }
    
    /**
     * 处理运行时异常（兜底）
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleRuntimeException(RuntimeException ex) {
        // TODO: 打印错误日志（System.err.println 或后续使用 @Slf4j）
        // 然后返回 Result.error(500, "系统内部错误")
        
        return null; // 替换为你的实现
    }
}
