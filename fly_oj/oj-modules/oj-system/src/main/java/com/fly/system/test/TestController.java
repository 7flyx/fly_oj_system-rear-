package com.fly.system.test;

import com.fly.common.redis.service.RedisService;
import com.fly.system.domain.SysUser;
import com.fly.system.test.domain.ValidationDTO;
import com.fly.system.test.service.ITestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private ITestService testService;

    @Autowired
    private RedisService redisService;

    @GetMapping("/list")
    public List<?> list() {
        return testService.list();
    }

    @GetMapping("/log")
    public String log() {
        log.info("hello world");
        log.error("我是error级别的");
        return "操作成功";
    }

    @GetMapping("/redisAddAndGet")
    public String redisAddAndGet() {
        SysUser sysUser = new SysUser();
        sysUser.setUserAccount("redisTest");
        redisService.setCacheObject("u", sysUser);
        SysUser us = redisService.getCacheObject("u", SysUser.class);
        return us.toString();
    }

    @GetMapping("/validation")
    public String validation(@Validated ValidationDTO validationDTO) {
        return "参数测试";
    }

}
