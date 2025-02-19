package com.fly.common.core.enums;

import lombok.Getter;

@Getter
public enum QuestionResType {
    ERROR(0), // 错误
    PASS(1), // 通过
    UN_SUBMIT(2), // 未提交
    IN_JUDGE(3); // 正在判题



    private Integer value;

    QuestionResType(Integer value) {
        this.value = value;
    }
}
