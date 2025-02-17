package com.fly.friend.service.user;

import com.fly.api.domain.vo.UserQuestionResultVO;
import com.fly.common.core.domain.R;
import com.fly.friend.domain.user.dto.UserSubmitDTO;

public interface IUserQuestionService {
    R<UserQuestionResultVO> submit(UserSubmitDTO userSubmitDTO);
}
