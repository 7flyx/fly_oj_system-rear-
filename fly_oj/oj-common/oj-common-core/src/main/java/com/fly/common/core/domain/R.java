package com.fly.common.core.domain;

import com.fly.common.core.enums.ResultCode;
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

    public static <T>R<T> success() {
        return success(ResultCode.SUCCESS);
    }

    public static <T>R<T> success(ResultCode resultCode) {
        return success(resultCode, null);
    }
    public static <T>R<T> success(ResultCode resultCode,  T data) {
        return new R<T>(resultCode.getCode(), resultCode.getMsg(), data);
    }

    public static <T>R<T> failed() {
        return failed(ResultCode.FAILED);
    }
    public static <T>R<T> failed(ResultCode resultCode) {
        return failed(resultCode, null);
    }

    public static <T>R<T> failed(ResultCode resultCode, T data) {
        return new R<T>(resultCode.getCode(), resultCode.getMsg(), data);
    }
}
