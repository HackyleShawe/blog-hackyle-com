package com.hackyle.blog.common.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IpUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IpUtils.class);

    private static String PUBLIC_IPv4;
    private static final String UNKNOWN = "unknown";


    /**
     * 获取本地公网的IP4地址
     * 主要思想：对某个网站发起一个HTTP请求，响应得到公网IP地址
     */
    public static String getPublicIpv4() {
        if(StringUtils.isNotBlank(PUBLIC_IPv4)) {
            return PUBLIC_IPv4;
        }

        Map<String, String> urlMap = new HashMap<>();
        urlMap.put("https://httpbin.org/get", "origin");
        urlMap.put("https://ipecho.net/plain", "");
        urlMap.put("https://ifconfig.co/json", "ip");

        Set<String> keySet = urlMap.keySet();
        for (String key : keySet) {
            String publicIP = fetchIP(key, 300, urlMap.get(key));
            if(StringUtils.isNotBlank(publicIP)) {
                PUBLIC_IPv4 = publicIP;
                return PUBLIC_IPv4;
            }
        }

        return "";
    }

    /**
     * 请求url获取自身的公网IP
     * @param url 例如：https://httpbin.org/get
     * @param connTimeout 连接超时
     * @param ipTag 从那个字段提取IP。为空表示直接返回，不需要额外提取
     * @return 公网IP
     */
    private static String fetchIP(String url, int connTimeout, String ipTag) {
        //私用方法，无须入参校验
        try {
            URL urlInstance = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlInstance.openConnection();
            connection.setConnectTimeout(connTimeout);
            if(200 == connection.getResponseCode()) {
                InputStream inputStream = connection.getInputStream();
                String originalResp = new String(inputStream.readAllBytes());

                if(null == ipTag || "".equals(ipTag)) {
                    return originalResp;
                }

                JSONObject respBody = JSON.parseObject(originalResp);

                return respBody.getString(ipTag);
            }
        } catch (IOException e) {
            LOGGER.error("获取公网IP异常-入参：url={},connTimeout={},ipTag={}", url, connTimeout, ipTag);
        }

        return "";
    }

    /**
     * 根据 HttpServletRequest 获取公网 IP
     */
    public static String getPublicIpv4(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String ip = request.getRemoteAddr();
        if(checkPublicIpv4(ip)) return ip;

        /*
         * X-Forwarded-For:简称XFF头，它代表客户端，也就是HTTP的请求端真实的IP
         * Squid服务代理:只有在通过了HTTP代理或者负载均衡服务器时才会添加该项
         * 标准格式如下：X-Forwarded-For: client_ip, proxy1_ip, proxy2_ip
         * 此头是可构造的，因此某些应用中应该对获取到的ip进行验证
         *
         * 在多级代理网络中，直接用getHeader("x-forwarded-for")可能获取到的是unknown信息，此时需要获取代理代理服务器重新包装的HTTP头信息
         */
        ip = request.getHeader("x-forwarded-for");
        if(checkPublicIpv4(ip)) return ip;

        ip = request.getHeader("X-Forwarded-For");
        if(checkPublicIpv4(ip)) return ip;

        /*
         * Apache 服务代理
         */
        ip = request.getHeader("Proxy-Client-IP");
        if(checkPublicIpv4(ip)) return ip;

        /*
         * WebLogic 服务代理
         */
        ip = request.getHeader("WL-Proxy-Client-IP");
        if(checkPublicIpv4(ip)) return ip;

        /*
         * Nginx服务代理
         */
        ip = request.getHeader("X-Real-IP");
        if(checkPublicIpv4(ip)) return ip;

        return "";
    }

    /**
     * 检查是否为公网IPv4
     * @param ip IPv4
     * @return true：是公网IP；false：不是公网IP
     */
    public static boolean checkPublicIpv4(String ip) {
        if(null == ip || "".equals(ip.trim()) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            return false;
        }

        String[] ipSeg = ip.split("\\."); //IPv4是以小数点分割的，IPv6是以冒号分割的
        if(ipSeg.length != 4) {
            return false;
        }

        int ipSeg01;
        int ipSeg02;

        try {
            ipSeg01 = Integer.parseInt(ipSeg[0]);
            ipSeg02 = Integer.parseInt(ipSeg[1]);
        } catch (Exception e) {
            return false;
        }

        // 回环地址：127.0.0.0 - 127.255.255.255
        if(ipSeg01 == 127) {
            return false;
        }

        if(ipSeg01 == 10) {
            return false;
        }

        if(ipSeg01 == 172 && (ipSeg02 >= 16 && ipSeg02 <= 31)) {
            return false;
        }

        if(ipSeg01 == 192 && ipSeg02 == 168) {
            return false;
        }

        return true;
    }

    /**
     * 检查是否为私网IPv4
     * @param ip IPv4
     * @return true：是私网IP；false：不是私网IP
     */
    public static boolean checkPrivateIpv4(String ip) {
        if(null == ip || "".equals(ip.trim())) {
            throw new IllegalArgumentException("The parameter can't be null!");
        }
        String[] ipSeg = ip.split("\\."); //IPv4是以小数点分割的，IPv6是以冒号分割的
        if(ipSeg.length != 4) {
            return false;
        }

        int ipSeg01;
        int ipSeg02;

        try {
            ipSeg01 = Integer.parseInt(ipSeg[0]);
            ipSeg02 = Integer.parseInt(ipSeg[1]);
        } catch (Exception e) {
            return false;
        }

        // 回环地址：127.0.0.0 - 127.255.255.255
        if(ipSeg01 == 127) {
            return false;
        }

        if(ipSeg01 == 10) {
            return true;
        }

        if(ipSeg01 == 172 && (ipSeg02 >= 16 && ipSeg02 <= 31)) {
            return true;
        }

        if(ipSeg01 == 192 && ipSeg02 == 168) {
            return true;
        }

        return false;
    }

}
