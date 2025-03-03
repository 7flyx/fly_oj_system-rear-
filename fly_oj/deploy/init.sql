
drop table if exists tb_sys_user;

-- 管理员用户
create table tb_sys_user (
    user_id bigint unsigned not null comment '用户id(主键)',
    user_account varchar(20) not null comment '账号',
    nick_name varchar(20) comment '昵称',
    password char(60) not null comment '密码',
    -- 以下是回溯相关的字段
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '更新人',
    update_time datetime comment '更新时间',
    primary key (`user_id`),
    unique key `idx_user_account` (`user_account`) -- 加索引
);

-- docker run -d -p 8848:8848 -p 9848:9848 --name oj-nacos -e MODE=standalone -e JVM_XMS=256m -e JVM_XMX=256m -e SPRING_DATASOURCE_PLATFORM=mysql -e MYSQL_SERVICE_HOST=172.17.0.3 -e MYSQL_SERVICE_PORT=3306 -e MYSQL_SERVICE_DB_NAME=flyoj_nacos_dev -e MYSQL_SERVICE_USER=ojtest -e MYSQL_SERVICE_PASSWORD=2001 nacos/nacos-server:v2.2.3

-- elasticsearch 相关

-- docker run -d --name oj-es-dev -e "ES_JAVA_OPTS=-Xms256m -Xmx256m" -e "discovery.type=single-node" -v E:\Github_Coding\fly_oj_system-rear-\fly_oj\deploy\dev\elasticSearch\es-plugins:/usr/share/elasticsearch/plugins -e "xpack.security.enabled=false" --privileged --network oj-network -p 9200:9200 -p 9300:9300 elasticsearch:8.5.3

-- docker run -d --name oj-kibana-dev -e "ELASTICSEARCH_HOSTS=http://oj-es-dev:9200" -e "I18N_LOCALE=zh-CN" -p15601:5601 --net=oj-network kibana:8.5.3

-- docker run -d --name oj-rabbit-dev -e RABBITMQ_DEFAULT_USER=admin -e RABBITMQ_DEFAULT_PASS=admin -p 15672:15672 -p 5672:5672 rabbitmq:3.8.30-management

--
-- B端：题目列表，添加题目，编辑，删除
-- C端：题目列表，答题，题目热榜列表，竞赛开始答题，竞赛练习等

create table tb_question(
    question_id bigint unsigned not null comment '题目id',
    title varchar(50) not null comment '题目标题',
    difficulty tinyint not null comment '题目难度 1简单，2中等，3困难',
    time_limit int not null comment '时间限制',
    space_limit int not null comment '空间限制',
    content varchar(10000) not null comment '题目内容',
    question_case varchar(1000) comment '题目用例',
    default_code varchar(1000) comment '默认代码块',
    main_func varchar(1000) comment 'main函数',
    -- 以下是回溯相关的字段
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '更新人',
    update_time datetime comment '更新时间',
    primary key (`question_id`)
);

-- 竞赛管理
-- B端：列表、新增、编辑、删除、发布、撤销发布
-- C端：列表（未开始、历史）、报名参赛、参加竞赛（竞赛倒计时、完成竞赛、竞赛内题目切换）、竞赛练习、查看排名、我的比赛、我的消息
-- 是否开始比赛，不需要放在数据库里，实时计算就行
create table tb_exam(
    exam_id bigint unsigned not null comment '竞赛id(主键)',
    title varchar(50) not null comment '标题',
    start_time datetime not null comment '竞赛开始时间',
    end_time datetime not null comment '竞赛结束时间',
    status tinyint not null default '0' comment '是否发布竞赛 0未发布，1已发布',
    -- 以下是回溯相关的字段
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '更新人',
    update_time datetime comment '更新时间',
    primary key (`exam_id`)
);

-- 题目与竞赛之间的关系
create table tb_exam_question(
    exam_question_id bigint unsigned not null comment '竞赛题目关系id(主键)',
    question_id bigint unsigned not null comment '题目id',
    exam_id bigint unsigned not null comment '竞赛id',
    question_order int not null comment '题目序号,比如第1题 第2题',
    -- 以下是回溯相关的字段
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '更新人',
    update_time datetime comment '更新时间',
    primary key (`exam_question_id`)
);


-- 用户管理
-- B端：列表、拉黑功能
-- C端：登录、注册、退出登录、个人中心
create table tb_user(
    user_id bigint unsigned not null comment '用户id(主键)',
    nick_name varchar(50) comment '昵称',
    head_image varchar(100) comment '用户头像/存储的图片的地址',
    sex tinyint comment '性别/1男/2女',
    phone char(11) not null comment '手机号(国内)',
    code char(6) comment '手机验证码',
    email varchar(30) comment '邮箱',
    wechat varchar(30) comment 'vx号',
    school_name varchar(50) comment '学校',
    major_name varchar(50) comment '专业',
    introduce varchar(200) comment '个人介绍',
    status tinyint not null comment '用户状态/拉黑0/正常1',
    -- 以下是回溯相关的字段
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '更新人',
    update_time datetime comment '更新时间',
    primary key(`user_id`)
);


-- 用户提交题目答案 表
create table tb_user_submit(
    submit_id bigint unsigned not null comment '提交记录id',
    user_id bigint unsigned not null comment '用户id',
    question_id bigint unsigned not null comment '题目id',
    exam_id bigint unsigned comment '竞赛id',
    program_type tinyint not null comment '代码类型 0：java，1：cpp，2：python3',
    user_code text not null comment '用户提交的代码',
    pass tinyint not null comment '0：未通过，1：已通过',
    case_judge_res varchar(1000) comment '测试用例输出结果',
    exe_message varchar(500) not null comment '执行结果',
    score int not null default '0' comment '得分',
    -- 以下是回溯相关的字段
    create_by bigint unsigned not null comment '创建人',
    create_time datetime not null comment '创建时间',
    update_by bigint unsigned comment '更新人',
    update_time datetime comment '更新时间',
    primary key(`submit_id`)
);




