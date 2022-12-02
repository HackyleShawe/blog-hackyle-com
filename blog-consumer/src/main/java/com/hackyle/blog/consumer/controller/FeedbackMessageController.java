package com.hackyle.blog.consumer.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.consumer.common.constant.ResponseEnum;
import com.hackyle.blog.consumer.common.pojo.ApiResponse;
import com.hackyle.blog.consumer.dto.FeedbackMessageAddDto;
import com.hackyle.blog.consumer.service.FeedbackMessageService;
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
@RequestMapping("/feedbackMessage")
public class FeedbackMessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackMessageController.class);

    @Autowired
    private FeedbackMessageService feedbackMessageService;

    @Autowired
    private ValueOperations<String, String> valueOperations;

    /**
     * 新增留言反馈
     */
    @PostMapping("/add")
    public ApiResponse<String> add(FeedbackMessageAddDto addDto, HttpServletRequest request) {
        LOGGER.info("新增留言反馈-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        if(addDto == null || StringUtils.isBlank(addDto.getName()) || StringUtils.isBlank(addDto.getEmail()) || StringUtils.isBlank(addDto.getContent())) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        //恶意提交判定：对某一IP，1天内限制提交5次
        if(badRequest(request, addDto)) {
            return ApiResponse.error(ResponseEnum.FRONT_END_ERROR.getCode(), ResponseEnum.FRONT_END_ERROR.getMessage(), "恶意请求，请稍后重试！");
        }

        try {
            return feedbackMessageService.add(addDto);
        } catch (Exception e) {
            LOGGER.error("新增留言反馈-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }


    private boolean badRequest(HttpServletRequest request, FeedbackMessageAddDto addDto) {
        String publicIpv4 = IpUtils.getPublicIpv4(request);
        String userAgent = request.getHeader("User-Agent");

        //1天内限制提交5次
        String reqKey = publicIpv4 + "::" + userAgent.replaceAll(" ", "");
        String reqVal = valueOperations.get(reqKey);
        if(StringUtils.isBlank(reqVal)) {
            valueOperations.set(reqKey, "1", 1, TimeUnit.DAYS);
        } else {
            LOGGER.info("新增留言反馈-恶意提交检查-reqKey={},reqVal={}", reqKey, reqVal);
            int reqValInt = Integer.parseInt(reqVal);
            if(reqValInt > 5) {
                return true;
            }
            valueOperations.increment(reqKey, 1);
        }

        return false;
    }

}
