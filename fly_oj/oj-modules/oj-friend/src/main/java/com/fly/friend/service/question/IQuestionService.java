package com.fly.friend.service.question;

import com.fly.common.core.domain.TableDataInfo;
import com.fly.friend.domain.question.QuestionQueryDTO;
import com.fly.friend.domain.question.vo.QuestionDetailVO;
import com.fly.friend.domain.question.vo.QuestionVO;

import java.util.List;

public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);

    QuestionDetailVO detail(Long questionId);

    String preQuestion(Long questionId);

    String nextQuestion(Long questionId);

    List<QuestionVO> hotList();
}
