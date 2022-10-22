package com.hackyle.blog.business.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class TestDemo {
    public static void main(String[] args) {

        JSONObject object = new JSONObject();
        object.put("location", "http://127.0.0.1:9000/blog/2022/10/1666336894513.png");
        //String data = "{\"location\":\"http://127.0.0.1:9000/blog/2022/10/1666336894513.png\"}";

        String s = JSON.toJSONString(object);
        System.out.println(s);


    }
}
