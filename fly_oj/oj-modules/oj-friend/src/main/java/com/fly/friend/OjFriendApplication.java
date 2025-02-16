package com.fly.friend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.fly.friend.**.mapper")
//@ComponentScan(basePackages = {"com.fly", "com.fly.common.file.config"}) // 添加子模块的包路径
public class OjFriendApplication {
    public static void main(String[] args) {
        SpringApplication.run(OjFriendApplication.class, args);
    }
}
