
drop table if exists tb_sys_user;

-- 管理员用户
create table tb_sys_user (
    user_id bigint unsigned not null comment '用户id(主键)',
    user_account varchar(20) not null comment '账号',
    password varchar(20) not null comment '密码',
    -- 以下是回溯相关的字段
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '更新人',
    update_time datetime comment '更新时间',
    primary key (`user_id`),
    unique key `idx_user_account` (`user_account`) -- 加索引
);

docker run -d -p 8848:8848 -p 9848:9848 --name oj-nacos -e MODE=standalone -e JVM_XMS=256m -e JVM_XMX=256m -e SPRING_DATASOURCE_PLATFORM=mysql -e MYSQL_SERVICE_HOST=172.17.0.3 -e MYSQL_SERVICE_PORT=3306 -e MYSQL_SERVICE_DB_NAME=flyoj_nacos_dev -e MYSQL_SERVICE_USER=ojtest -e MYSQL_SERVICE_PASSWORD=2001 nacos/nacos-server:v2.2.3