package com.fly.friend.service.question;

import com.fly.common.core.domain.TableDataInfo;
import com.fly.friend.domain.question.QuestionQueryDTO;
import com.fly.friend.domain.question.vo.QuestionDetailVO;

public interface IQuestionService {
    TableDataInfo list(QuestionQueryDTO questionQueryDTO);

    QuestionDetailVO detail(Long questionId);

    String preQuestion(Long questionId);

    String nextQuestion(Long questionId);
}
