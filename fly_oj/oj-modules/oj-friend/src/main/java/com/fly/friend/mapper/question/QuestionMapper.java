package com.fly.friend.mapper.question;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fly.friend.domain.question.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
