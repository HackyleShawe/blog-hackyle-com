package com.hackyle.blog.business.controller;

import com.alibaba.fastjson2.JSON;
import com.hackyle.blog.common.constant.ResponseEnum;
import com.hackyle.blog.common.pojo.ApiRequest;
import com.hackyle.blog.common.pojo.ApiResponse;
import com.hackyle.blog.business.dto.FeedbackMessageAddDto;
import com.hackyle.blog.business.qo.FeedbackMessageQo;
import com.hackyle.blog.business.service.FeedbackMessageService;
import com.hackyle.blog.business.vo.FeedbackMessageVo;
import com.hackyle.blog.common.dto.PageRequestDto;
import com.hackyle.blog.common.dto.PageResponseDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feedbackMessage")
public class FeedbackMessageController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackMessageController.class);

    @Autowired
    private FeedbackMessageService feedbackMessageService;

    /**
     * 新增留言反馈
     */
    @PostMapping("/add")
    public ApiResponse<String> add(@RequestBody ApiRequest<FeedbackMessageAddDto> apiRequest) {
        FeedbackMessageAddDto addDto = apiRequest.getData();
        LOGGER.info("新增留言反馈-Controller层入参-addDto={}", JSON.toJSONString(addDto));

        if(addDto == null) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return feedbackMessageService.add(addDto);
        } catch (Exception e) {
            LOGGER.error("新增留言反馈-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 删除留言反馈
     */
    @DeleteMapping("/del")
    public ApiResponse<String> del(@RequestParam("ids")String ids) {
        LOGGER.info("删除留言反馈-Controller层入参-ids={}", ids);
        if(StringUtils.isBlank(ids)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return feedbackMessageService.del(ids);
        } catch (Exception e) {
            LOGGER.error("删除留言反馈-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 修改留言反馈
     */
    @PutMapping("/update")
    public ApiResponse<String> update(@RequestBody ApiRequest<FeedbackMessageAddDto> apiRequest) {
        FeedbackMessageAddDto updateDto = apiRequest.getData();
        LOGGER.info("修改留言反馈-Controller层入参-updateDto={}", JSON.toJSONString(updateDto));

        if(updateDto == null || updateDto.getId() == null) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            return feedbackMessageService.update(updateDto);
            //return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage());
        } catch (Exception e) {
            LOGGER.error("修改留言反馈-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 获取留言反馈详情
     */
    @GetMapping("/fetch")
    public ApiResponse<FeedbackMessageVo> fetch(@RequestParam("id")String id) {
        LOGGER.info("获取留言反馈详情-controller层入参-id={}", id);
        if(StringUtils.isBlank(id)) {
            return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
        }

        try {
            FeedbackMessageVo feedbackMessageVo = feedbackMessageService.fetch(id);

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), feedbackMessageVo);
        } catch (Exception e) {
            LOGGER.error("获取留言反馈详情-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }

    /**
     * 分页获取所有留言反馈
     */
    @PostMapping("/fetchList")
    public ApiResponse<PageResponseDto<FeedbackMessageVo>> fetchList(@RequestBody ApiRequest<PageRequestDto<FeedbackMessageQo>> apiRequest) {
        PageRequestDto<FeedbackMessageQo> pageRequestDto = apiRequest.getData();
        LOGGER.info("分页获取所有留言反馈-Controller层入参-pageRequestDto={}", JSON.toJSONString(pageRequestDto));

        if(pageRequestDto == null) {
            //return ApiResponse.error(ResponseEnum.PARAMETER_MISSING.getCode(), ResponseEnum.PARAMETER_MISSING.getMessage());
            pageRequestDto = new PageRequestDto<>();
            pageRequestDto.setCurrentPage(1);
            pageRequestDto.setPageSize(10);
        }

        //纠正不合法数据
        if(pageRequestDto.getPageSize() < 1) {
            pageRequestDto.setPageSize(10);
        }
        if(pageRequestDto.getCurrentPage() < 1) {
            pageRequestDto.setCurrentPage(1);
        }

        try {
            PageResponseDto<FeedbackMessageVo> pageResponseDto = feedbackMessageService.fetchList(pageRequestDto);
            LOGGER.info("获取所有留言反馈-Controller层出参-pageResponseDto={}", JSON.toJSONString(pageResponseDto));

            return ApiResponse.success(ResponseEnum.OP_OK.getCode(), ResponseEnum.OP_OK.getMessage(), pageResponseDto);
        } catch (Exception e) {
            LOGGER.error("获取所有留言反馈-出现异常：", e);
            return ApiResponse.error(ResponseEnum.EXCEPTION.getCode(), ResponseEnum.EXCEPTION.getMessage());
        }
    }
}
