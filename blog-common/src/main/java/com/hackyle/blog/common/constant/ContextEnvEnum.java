package com.hackyle.blog.common.constant;

public enum ContextEnvEnum {
    DEV("dev", "development"),
    TEST("test", "test"),
    PROD("prod", "production");

    private final String name;
    private final String desc;

    ContextEnvEnum(String name, String desc) {
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
