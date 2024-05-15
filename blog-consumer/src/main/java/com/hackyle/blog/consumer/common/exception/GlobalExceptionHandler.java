package com.hackyle.blog.consumer.common.exception;

import com.hackyle.blog.common.constant.ResponseEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 * ControllerAdvice表示这是一个控制器增强类，当控制器发生异常且符合类中定义的拦截异常类，将会被拦截。
 * 在捕获异常时是按照异常方法的顺序依次捕获(类似于catch关键字后的捕获顺序)，所以需要将顶级的异常放在最后
 * 注意：各个异常处理方法要同名，采取重载的形式
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    /**
     * 前后端不分离时，捕捉到异常，指定重定向到错误页面
     */
    @ExceptionHandler(value = NotFoundException.class)
    public ModelAndView notFoundExceptionHandler(HttpServletRequest request, Exception exception) {
        logger.error("出现NotFoundException异常：exception=", exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", ResponseEnum.EXCEPTION.getCode());
        modelAndView.addObject("message", ResponseEnum.EXCEPTION.getMessage());
        modelAndView.addObject("url", request.getRequestURL());
        modelAndView.setViewName("common/404");
        return modelAndView;
    }


    @ExceptionHandler(value = Exception.class)
    public ModelAndView exceptionHandler(HttpServletRequest request, Exception exception) {
        logger.error("出现Exception异常：exception=", exception);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("code", ResponseEnum.EXCEPTION.getCode());
        modelAndView.addObject("message", ResponseEnum.EXCEPTION.getMessage());
        modelAndView.addObject("url", request.getRequestURL());
        modelAndView.setViewName("common/500");
        return modelAndView;
    }

    /*
     * 处理并捕获Exception这个大异常
     */
    //@ResponseBody
    //@ExceptionHandler(Exception.class)
    //public ApiResponse<Exception> handler(HttpServletRequest request, Exception exception) {
    //    logger.error("出现Exception异常：exception=", exception);
    //    return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
    //}
}
