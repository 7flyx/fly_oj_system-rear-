package com.fly.common.core.enums;

public enum UserStatus {
    NORMAL(1), // 正常
    BLOCK(0); // 拉黑
    private Integer value;

    UserStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }
}
