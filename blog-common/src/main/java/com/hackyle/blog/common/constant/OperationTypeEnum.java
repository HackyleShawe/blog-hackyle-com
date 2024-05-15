package com.hackyle.blog.common.constant;

public enum OperationTypeEnum {
    //三大主流的操作系统
    WIN("win", "Windows"),
    LINUX("linux", "Linux"),
    MAC("mac", "Mac"),

    //细化的操作系统类型
    CENTOS("centos", "CentOS"),
    DEBIAN("debian", "Debian"),
    UBUNTU("ubuntu", "Ubuntu")
    ;

    private final String name;
    private final String desc;

    OperationTypeEnum(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
