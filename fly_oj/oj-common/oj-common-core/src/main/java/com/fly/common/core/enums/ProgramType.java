package com.fly.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProgramType {
    JAVA(0, "java语言"),
    CPP(1, "cpp语言"),
    PYTHON3(2, "python3")
    ;
    private Integer value;
    private String program;

    @Override
    public String toString() {
        return super.toString();
    }
}
