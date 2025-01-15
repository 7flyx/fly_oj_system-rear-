package com.fly.common.core.domain;

import lombok.Data;

// 返回数据类型，统一格式
@Data
public class R<T> {
    private int code;
    private String msg;
    private T data;

    public R() {
    }
    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T>R<T> success(int code, String msg) {
        return success(code, msg, null);
    }
    public static <T>R<T> success(int code, String msg, T data) {
        return new R<T>(code, msg, data);
    }

    public static <T>R<T> failed(int code, String msg) {
        return failed(code, msg, null);
    }

    public static <T>R<T> failed(int code, String msg, T data) {
        return new R<T>(code, msg, data);
    }
}
