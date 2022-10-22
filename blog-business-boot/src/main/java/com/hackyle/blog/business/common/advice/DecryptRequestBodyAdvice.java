package com.hackyle.blog.business.common.advice;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.hackyle.blog.business.util.IDUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 在进入'@RequestBody'之前进行加密操作
 * RequestBodyAdviceAdapter：进入POST请求的'@RequestBody'之前调用
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DecryptRequestBodyAdvice extends RequestBodyAdviceAdapter {
    /**
     * 只有supports方法返回true，才会执行beforeBodyWrite方法
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Type type, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    /**
     * 进入POST请求的'@RequestBody'之前执行
     * 业务逻辑：将ID解密为long类型
     */
    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter,
                                           Type targetType, Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        byte[] requestDataByte = new byte[inputMessage.getBody().available()];
        inputMessage.getBody().read(requestDataByte);
        InputStream resultInputStream = new ByteArrayInputStream(requestDataByte);

        if(requestDataByte.length > 0) {
            String requestBody = new String(requestDataByte, StandardCharsets.UTF_8);
            JSONObject requestObj = JSON.parseObject(requestBody);

            //处理顶层请求体中的id，没有使用ApiRequest包裹的id
            Object id = requestObj.get("id");
            if(null != id) {
                requestObj.put("id", IDUtils.decryptByAES(String.valueOf(id)));
            }

            //处理请求体中的第二层ID：ApiRequest#data中的id
            Object data = requestObj.get("data");
            if(null != data) {
                JSONObject dataObject = JSON.parseObject(JSON.toJSONString(data));
                Object dataId = dataObject.get("id");
                if(null != dataId && StringUtils.isNotBlank(String.valueOf(dataId))) {
                    dataObject.put("id", IDUtils.decryptByAES(String.valueOf(dataId)));
                }


                otherIdHandler(dataObject);

                //处理请求体中的第三层ID：Apirequest#data#PageRequestDto#condition中的id
                Object condition = dataObject.get("condition");
                if(null != condition) {
                    JSONObject conditionObj = JSON.parseObject(JSON.toJSONString(condition));
                    Object condId = conditionObj.get("id");
                    if(null != condId) {
                        dataObject.put("id", IDUtils.decryptByAES(String.valueOf(condId)));
                    }
                    dataObject.put("condition", conditionObj);
                }

                requestObj.put("data", dataObject);
            }

            resultInputStream = new ByteArrayInputStream(requestObj.toJSONString().getBytes(StandardCharsets.UTF_8));
        }

        InputStream finalResultInputStream = resultInputStream;
        return new HttpInputMessage() {
            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }

            @Override
            public InputStream getBody() throws IOException {
                return finalResultInputStream;
            }
        };
    }

    private void otherIdHandler(JSONObject dataObject) {
        Object authorIds = dataObject.get("authorIds");
        if(null != authorIds && StringUtils.isNotBlank(String.valueOf(authorIds))) {
            String decryptIds = idsHandler(String.valueOf(authorIds));
            dataObject.put("authorIds", decryptIds);
        }

        Object categoryIds = dataObject.get("categoryIds");
        if(null != categoryIds && StringUtils.isNotBlank(String.valueOf(categoryIds))) {
            String decryptIds = idsHandler(String.valueOf(categoryIds));
            dataObject.put("categoryIds", decryptIds);
        }

        Object tagIds = dataObject.get("tagIds");
        if(null != tagIds && StringUtils.isNotBlank(String.valueOf(tagIds))) {
            String decryptIds = idsHandler(String.valueOf(tagIds));
            dataObject.put("tagIds", decryptIds);
        }

    }

    private String idsHandler(String encryptIds) {
        //String authorIdsStr = String.valueOf(encryptIds);
        String[] encryptIdsArr = encryptIds.split(",");
        StringBuilder sb = new StringBuilder();
        for (String id : encryptIdsArr) {
            long decryptId = IDUtils.decryptByAES(String.valueOf(id));
            sb.append(decryptId).append(",");
        }

        return sb.toString();
    }
}
