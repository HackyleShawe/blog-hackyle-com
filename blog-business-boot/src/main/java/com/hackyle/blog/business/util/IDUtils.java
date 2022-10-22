package com.hackyle.blog.business.util;

import org.apache.commons.lang3.StringUtils;

/**
 * ID混淆工具类
 * 注意：
 *  混淆规则不应该轻易改动
 *  由于混淆后的ID可能会用作URL的一部分，需要替换Base64中的不符合URL规则的字符
 */
public class IDUtils {
    private static final String DEFAULT_PW = "blog.hackyle.com:default.pw";

    public static long timestampID() {
        return System.currentTimeMillis();
    }

    /**
     * 将ID对称加密加密
     * Base64的定义：用 64 个可打印字符来表示二进制数据，小写字母 a-z、大写字母 A-Z、数字 0-9、符号"+"、"/"（再加上作为垫字的"="，实际上是 65 个字符）。
     */
    public static String encryptByAES(long id) {
        String encrypt = AESUtils.encrypt(String.valueOf(id), DEFAULT_PW);

        //由于返回的是Base64字符串，作为URL时可能存在冲突，所以再进行转换
        encrypt = encrypt.replaceAll("\\+", "-")
                .replaceAll("/", ".")
                .replaceAll("=","_");
        return encrypt;
    }

    /**
     * 将ID对称解密
     */
    public static long decryptByAES(String encryptedId) {
        if(StringUtils.isBlank(encryptedId)) {
            throw new RuntimeException("encryptedId为空");
        }

        encryptedId = encryptedId.replaceAll("-", "+")
                .replaceAll("\\.", "/")
                .replaceAll("_","=");

        String decryptedId = AESUtils.decrypt(encryptedId, DEFAULT_PW);
        return Long.parseLong(decryptedId);
    }


    public static void main(String[] args) throws Exception {
        long start = timestampID();
        testAES();
        System.out.println(timestampID() - start);
    }


    private static void testAES() {
        long id = timestampID();
        System.out.println(id);
        String encryptByAES = encryptByAES(id);
        System.out.println(encryptByAES);

        long decryptByAES = decryptByAES(encryptByAES);
        System.out.println(decryptByAES);
    }
}
