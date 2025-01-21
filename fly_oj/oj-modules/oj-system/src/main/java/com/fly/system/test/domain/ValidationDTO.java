package com.fly.system.test.domain;


import jakarta.validation.constraints.*;

public class ValidationDTO {
    @NotBlank(message = "用户账号不能为空")
    private String userAccount;
    @NotBlank(message = "用户密码不能为空")
    @Size(min = 5, max = 10, message = "密码长度不能少于6位，不能大于10位")
    private String password;

    @Min(value = 0, message = "年龄不能小于0岁")
    @Max(value = 60, message = "年龄不能大于60岁")
    private  int age;

    @Email(message = "必须符合邮箱格式")
    private String email;
    private String phone;

}
