package com.yh.register.common.handler;

import com.yh.register.common.exception.RegisterException;
import com.yh.register.common.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类
 * 对controller面向切面，对功能进行增强
 */
@ControllerAdvice  //对controller面向切面，可结合@ExceptionHandler注解进行全局异常处理
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.fail();
    }

    /**
     * 自定义异常处理方法
     * @param e
     * @return
     */
    @ExceptionHandler(RegisterException.class)
    @ResponseBody
    public Result error(RegisterException e){
        e.printStackTrace();
        return Result.build(e.getCode(), e.getMessage());
    }
}
