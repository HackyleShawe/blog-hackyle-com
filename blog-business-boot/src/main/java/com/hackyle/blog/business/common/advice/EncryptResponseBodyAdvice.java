package com.hackyle.blog.business.common.advice;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.hackyle.blog.business.util.IDUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 在响应'@ResponseBody'之前进行解密操作
 * ResponseBodyAdvice：进入POST请求的'@RequestBody'之前调用
 */
@ControllerAdvice
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice {
    /**
     * 只有supports方法返回true，才会执行beforeBodyWrite方法
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        return true;
    }

    /**
     * 在POST响应'@ResponseBody'之前执行
     * 业务逻辑：将接口内的数据转为JSON，把里面的id进行加密，再响应给前端
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        String respObjString = JSON.toJSONString(o);

        if(o instanceof String) {
            //就已经是字符串了，就不用再执行JSON.toJSONString
            respObjString = (String) o;
        }

        JSONObject obj = JSON.parseObject(respObjString);
        Object data = obj.get("data");
        if(null == data) {
            return o;
        }

        Object state = obj.get("state");
        if(null == state || !"true".equalsIgnoreCase(String.valueOf(state))) {
            return o;
        }

        String dataJson = JSON.toJSONString(data);
        if(dataJson.startsWith("{")) {
            //是一个对象
            JSONObject dataObject = JSON.parseObject(dataJson);
            Object id = dataObject.get("id");
            if(null != id) {
                dataObject.put("id", IDUtils.encryptByAES(Long.parseLong(String.valueOf(id))));
            }

            //处理分页数据中id：ApiResponse#dataPageResponse#rows
            JSONArray rowsArray = dataObject.getJSONArray("rows");
            if(null != rowsArray && rowsArray.size() > 0) {
                JSONArray tempArr = new JSONArray(rowsArray.size());

                int len = rowsArray.size();
                for (int i = 0; i < len; i++) {
                    JSONObject rowJSONObj = JSON.parseObject(JSON.toJSONString(rowsArray.get(i)));
                    Object rowId = rowJSONObj.get("id");
                    if(null != rowId) {
                        rowJSONObj.put("id", IDUtils.encryptByAES(Long.parseLong(String.valueOf(rowId))));
                    }
                    tempArr.set(i, rowJSONObj);
                }

                dataObject.put("rows", tempArr);
            }
            obj.put("data", dataObject);


        } else if(dataJson.startsWith("[")) {
            //是一个数组
            //处理ApiResponse中的id
            JSONArray dataArr = JSON.parseArray(dataJson);
            if(null == dataArr || dataArr.size() < 1) {
                return o;
            }
            JSONArray resultArr = new JSONArray();
            for (Object dataEle : dataArr) {
                JSONObject dataObject = JSON.parseObject(JSON.toJSONString(dataEle));
                Object id = dataObject.get("id");
                if(null != id) {
                    dataObject.put("id", IDUtils.encryptByAES(Long.parseLong(String.valueOf(id))));
                }

                //处理分页数据中id：ApiResponse#dataPageResponse#rows
                JSONArray rowsArray = dataObject.getJSONArray("rows");
                if(null != rowsArray && rowsArray.size() > 0) {
                    JSONArray tempArr = new JSONArray(rowsArray.size());

                    int len = rowsArray.size();
                    for (int i = 0; i < len; i++) {
                        JSONObject rowJSONObj = JSON.parseObject(JSON.toJSONString(rowsArray.get(i)));
                        Object rowId = rowJSONObj.get("id");
                        if(null != rowId) {
                            rowJSONObj.put("id", IDUtils.encryptByAES(Long.parseLong(String.valueOf(rowId))));
                        }
                        tempArr.set(i, rowJSONObj);
                    }

                    dataObject.put("rows", tempArr);
                }

                resultArr.add(dataObject);
            }
            obj.put("data", resultArr);
        }

        return JSON.parse(JSON.toJSONString(obj));
    }
}

