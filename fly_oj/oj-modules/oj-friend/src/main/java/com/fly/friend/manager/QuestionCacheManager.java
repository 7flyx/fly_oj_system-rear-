package com.fly.friend.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fly.common.core.constants.CacheConstants;
import com.fly.common.core.enums.ResultCode;
import com.fly.common.redis.service.RedisService;
import com.fly.common.security.exception.ServiceException;
import com.fly.friend.domain.question.Question;
import com.fly.friend.mapper.question.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionCacheManager {
    @Autowired
    private RedisService redisService;

    @Autowired
    private QuestionMapper questionMapper;

    public Long getListSize() {
        return redisService.getListSize(CacheConstants.QUESTION_LIST);
    }

    public void refreshCache() {
        List<Question> questions = questionMapper.selectList(new LambdaQueryWrapper<Question>()
                .select(Question::getQuestionId)
                .orderByDesc(Question::getCreateTime));
        if (CollectionUtil.isEmpty(questions)) {
            return;
        }
        List<Long> list = questions.stream().map(Question::getQuestionId).toList();
        // 尾插法 到 缓存中
        redisService.rightPushAll(CacheConstants.QUESTION_LIST, list);
    }

    public Long preQuestion(Long questionId) {
        Long index = redisService.indexOfForList(CacheConstants.QUESTION_LIST, questionId);
        if (index == 0) {
            throw new ServiceException(ResultCode.FAILED_FIRST_QUESTION);
        }
        return redisService.indexForList(CacheConstants.QUESTION_LIST, index - 1, Long.class);
    }

    public Long nextQuestion(Long questionId) {
        Long index = redisService.indexOfForList(CacheConstants.QUESTION_LIST, questionId);
        long lastIndex  = getListSize() - 1;
        if (index == lastIndex) {
            throw new ServiceException(ResultCode.FAILED_LAST_QUESTION);
        }
        return redisService.indexForList(CacheConstants.QUESTION_LIST, index + 1, Long.class);
    }
}
