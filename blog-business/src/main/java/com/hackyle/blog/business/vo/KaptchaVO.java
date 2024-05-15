package com.hackyle.blog.business.vo;

/**
 * 验证码
 */
public class KaptchaVO {

    /**
     *  验证码UUID
     */
    private String uuid;

    /**
     * base64 验证码
     */
    private String code;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
