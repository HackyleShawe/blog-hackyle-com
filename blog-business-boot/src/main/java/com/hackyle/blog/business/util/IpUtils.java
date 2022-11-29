package com.hackyle.blog.business.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class IpUtils {
    private static String PUBLIC_IPv4;

    /**
     * 获取本地公网的IP4地址
     * 主要思想：对某个网站发起一个HTTP请求，响应得到公网IP地址
     */
    public static String getPublicIpv4() {
        if(StringUtils.isNotBlank(PUBLIC_IPv4)) {
            return PUBLIC_IPv4;
        }

        try {
            URL urlIpecho = new URL("https://ipecho.net/plain");
            HttpURLConnection urlConnIpecho = (HttpURLConnection) urlIpecho.openConnection();
            if(200 == urlConnIpecho.getResponseCode()) {
                InputStream inputStream = urlConnIpecho.getInputStream();
                String ip = new String(inputStream.readAllBytes());
                if(StringUtils.isNotBlank(ip)) {
                    PUBLIC_IPv4 = ip;
                    return PUBLIC_IPv4;
                }
            }

            URL urlCo = new URL("https://ifconfig.co/json");
            HttpURLConnection urlConnectionCo = (HttpURLConnection) urlCo.openConnection();
            if(200 == urlConnectionCo.getResponseCode()) {
                InputStream inputStream = urlConnectionCo.getInputStream();
                if(null != inputStream) {
                    JSONObject respBody = JSON.parseObject(new String(inputStream.readAllBytes()));
                    String ip = respBody.get("ip").toString();
                    if(StringUtils.isNotBlank(ip)) {
                        PUBLIC_IPv4 = ip;
                        return PUBLIC_IPv4;
                    }
                }
            }

        }catch (IOException e) {
            //e.printStackTrace();
        }

        return "";
    }

    /*
     * 获取本地私网IPv4地址
     * 主要思想：通过网络适配器（包括物理的、虚拟的）获取
     */
    public static String getPrivateIpv4ByNetworkInterface() {
        try {
            final List<Inet4Address> inet4Addresses = getLocalIp4AddressFromNetworkInterface();

            if (inet4Addresses.size() != 1) {
                final Optional<Inet4Address> ipBySocketOpt = getIpBySocket();
                if (ipBySocketOpt.isPresent()) {
                    return ipBySocketOpt.map(Inet4Address::getHostAddress).orElse(null);
                } else {
                    return inet4Addresses.isEmpty() ? null : inet4Addresses.get(0).getHostAddress();
                }
            }

            return inet4Addresses.get(0).getHostAddress();
        } catch (SocketException e) {
            return "";
        }
    }

    /*
     * 获取本机所有网卡信息   得到所有IP信息
     * @return Inet4Address>
     */
    public static List<Inet4Address> getLocalIp4AddressFromNetworkInterface() throws SocketException {
        List<Inet4Address> addresses = new ArrayList<>(1);
        // 所有网络接口信息
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        if (ObjectUtils.isEmpty(networkInterfaces)) {
            return addresses;
        }
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            //滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
            if (!isValidInterface(networkInterface)) {
                continue;
            }
            // 所有网络接口的IP地址信息
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                // 判断是否是IPv4，并且内网地址并过滤回环地址.
                if (isValidAddress(inetAddress)) {
                    addresses.add((Inet4Address) inetAddress);
                }
            }
        }
        return addresses;
    }

    /**
     * 过滤回环网卡、点对点网卡、非活动网卡、虚拟网卡并要求网卡名字是eth或ens开头
     * @param ni 网卡
     * @return 如果满足要求则true，否则false
     */
    private static boolean isValidInterface(NetworkInterface ni) throws SocketException {
        return !ni.isLoopback() && !ni.isPointToPoint() && ni.isUp() && !ni.isVirtual()
                && (ni.getName().startsWith("eth") || ni.getName().startsWith("ens"));
    }

    /**
     * 判断是否是IPv4，并且内网地址并过滤回环地址
     */
    private static boolean isValidAddress(InetAddress address) {
        return address instanceof Inet4Address && address.isSiteLocalAddress() && !address.isLoopbackAddress();
    }

    /*
     * 通过Socket 唯一确定一个IP
     * 当有多个网卡的时候，使用这种方式一般都可以得到想要的IP。甚至不要求外网地址8.8.8.8是可连通的
     */
    private static Optional<Inet4Address> getIpBySocket() throws SocketException {
        try (final DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            if (socket.getLocalAddress() instanceof Inet4Address) {
                return Optional.of((Inet4Address) socket.getLocalAddress());
            }
        } catch (UnknownHostException networkInterfaces) {
            throw new RuntimeException(networkInterfaces);
        }
        return Optional.empty();
    }


    public static void main(String[] args) throws Exception {
        System.out.println(getPrivateIpv4ByNetworkInterface());

        String publicIpv4 = getPublicIpv4();
        System.out.println(publicIpv4);

    }
}
