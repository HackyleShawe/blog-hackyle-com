package com.hackyle.blog.consumer.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.consumer.common.constant.ResponseEnum;
import com.hackyle.blog.consumer.common.pojo.ApiResponse;
import com.hackyle.blog.consumer.dto.CommentAddDto;
import com.hackyle.blog.consumer.service.CommentService;
import com.hackyle.blog.consumer.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/comment")
public class CommentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    private CommentService commentService;

    @Autowired
    private ValueOperations<String, String> valueOperations;


    /**
     * 新增文章评论
     */
    @PostMapping("/add")
    public ApiResponse<String> add(CommentAddDto commentAddDto, HttpServletRequest request) {
        LOGGER.info("新增文章评论-Controller层入参-commentAddDto={}", JSON.toJSONString(commentAddDto));
        if(commentAddDto == null || StringUtils.isBlank(commentAddDto.getTargetId()) ||
                StringUtils.isBlank(commentAddDto.getName()) || StringUtils.isBlank(commentAddDto.getEmail())
                || StringUtils.isBlank(commentAddDto.getContent())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        //恶意提交判定：对某一IP，6小时内对某target限制提交3次，对某父评论限制提交5次
        if(badRequest(request, commentAddDto)) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage(), "恶意请求，请稍后重试！");
        }

        try {
            return commentService.add(commentAddDto);
        } catch (Exception e) {
            LOGGER.error("新增文章评论-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    private boolean badRequest(HttpServletRequest request, CommentAddDto commentAddDto) {
        String publicIpv4 = IpUtils.getPublicIpv4(request);

        //6小时内对某target限制提交3次
        String requestTarget = publicIpv4 + "::" + commentAddDto.getTargetId();
        String requestTargetVal = valueOperations.get(requestTarget);
        if(StringUtils.isBlank(requestTargetVal)) {
            valueOperations.set(requestTarget, "1", 6, TimeUnit.HOURS);
        } else {
            LOGGER.info("新增文章评论-恶意提交检查-requestTarget={},requestTargetVal={}", requestTarget, requestTargetVal);
            int reqValInt = Integer.parseInt(requestTargetVal);
            if(reqValInt > 3) {
                return true;
            }
            valueOperations.increment(requestTarget, 1);
        }

        //6小时内对某父评论限制提交5次
        if(StringUtils.isNotBlank(commentAddDto.getParentId())) {
            String requestParent = publicIpv4 + "::" + commentAddDto.getTargetId() + "::" + commentAddDto.getParentId();
            String requestParentVal = valueOperations.get(requestParent);
            if(StringUtils.isBlank(requestParentVal)) {
                valueOperations.set(requestParent, "1", 6, TimeUnit.HOURS);
            } else {
                LOGGER.info("新增文章评论-恶意提交检查-requestParent={},requestParentVal={}", requestParent, requestParentVal);
                int reqValInt = Integer.parseInt(requestParentVal);
                if(reqValInt > 5) {
                    return true;
                }
                valueOperations.increment(requestParent, 1);
            }
        }
        return false;
    }

}
