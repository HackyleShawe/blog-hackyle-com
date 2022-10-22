package com.hackyle.blog.business.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.hackyle.blog.business.common.constant.ResponseEnum;
import com.hackyle.blog.business.common.pojo.ApiResponse;
import com.hackyle.blog.business.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;


/**
 * 访问拦截器
 */
@Component
public class AccessInterceptor implements HandlerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");

        String requestURI = request.getRequestURI();
        String token = request.getHeader("Authorization");
        token = StringUtils.isBlank(token) ? request.getHeader("X-Token") : token;
        token = StringUtils.isBlank(token) ? request.getParameter("token") : token;

        //放行：token不为空、token有效
        if(StringUtils.isNotBlank(token) && JwtUtils.validateJWT(token)) {
            String newToken = JwtUtils.refreshCheck(token);
            response.setHeader("Authorization", newToken);
            LOGGER.info("{}-Token有效，旧的Token={}，新的Token={}", requestURI, token, newToken);
            return true; //拦截器放行

        } else {
            ServletOutputStream outputStream = response.getOutputStream();
            response.setContentType("text/html;charset=UTF-8");
            String info = requestURI + " - Token为空或Token无效";
            LOGGER.info(info);
            ApiResponse<String> error = ApiResponse.error(ResponseEnum.OP_FAIL.getCode(), ResponseEnum.OP_FAIL.getMessage(), info);
            outputStream.write(JSON.toJSONString(error).getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            return false;
        }
    }
}
