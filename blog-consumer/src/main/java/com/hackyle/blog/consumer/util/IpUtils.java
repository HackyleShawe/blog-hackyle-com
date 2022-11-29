package com.hackyle.blog.consumer.util;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IpUtils {
    private static final String UNKNOWN = "unknown";

    /** IPv4正则 */
    private static final String IPv4_REGEX = "^(?:(?:\\\\d|[1-9]\\\\d|1\\\\d\\\\d|2[0-4]\\\\d|25[0-5])\\\\.){3}(?:\\\\d|[1-9]\\\\d|1\\\\d\\\\d|2[0-4]\\\\d|25[0-5])$";
    /** 私网IPv4正则 */
    private static final String IPv4_PRIVATE_REGEX = "^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$";
    /** 公网IPv4正则 */
    private static final String IPv4_PUBLIC_REGEX = "^(((?!(127\\\\.0\\\\.0\\\\.1)|(localhost)|(10\\\\.\\\\d{1,3}\\\\.\\\\d{1,3}\\\\.\\\\d{1,3})|(172\\\\.((1[6-9])|(2\\\\d)|(3[01]))\\\\.\\\\d{1,3}\\\\.\\\\d{1,3})|(192\\\\.168\\\\.\\\\d{1,3}\\\\.\\\\d{1,3})).)*)(?:(?:\\\\d|[1-9]\\\\d|1\\\\d\\\\d|2[0-4]\\\\d|25[0-5])\\\\.){3}(?:\\\\d|[1-9]\\\\d|1\\\\d\\\\d|2[0-4]\\\\d|25[0-5])$";
    private static final Pattern IPv4_PRIVATE_PATTERN = Pattern.compile(IPv4_PRIVATE_REGEX);
    private static final Pattern IPv4_PUBLIC_PATTERN = Pattern.compile(IPv4_PUBLIC_REGEX);

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

        Matcher matcher = IPv4_PUBLIC_PATTERN.matcher(ip);
        return matcher.find();
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
        Matcher matcher = IPv4_PRIVATE_PATTERN.matcher(ip);
        return matcher.find();
    }

    /**
     * 检查IP是否为空或者UNKNOWN
     * @return true:是空的；false:不是空的
     */
    private static boolean checkEmptyIp(String ip) {
        return ip == null || "".equals(ip.trim()) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip);
    }

}
