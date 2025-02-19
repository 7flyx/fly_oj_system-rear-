package com.fly.judge.rabbit;

import com.fly.api.domain.dto.JudgeSubmitDTO;
import com.fly.common.core.constants.RabbitMQConstants;
import com.fly.judge.service.IJudgeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// 消费者
@Slf4j
@Component
public class JudgeConsumer {
    @Autowired
    private IJudgeService judgeService;

    @RabbitListener(queues = RabbitMQConstants.OJ_WORK_QUEUE) // 监听这个队列
    public void consume(JudgeSubmitDTO judgeSubmitDTO) {
        log.info("收到消息: {}", judgeSubmitDTO);
        judgeService.doJudgeJavaCode(judgeSubmitDTO);
    }
}
