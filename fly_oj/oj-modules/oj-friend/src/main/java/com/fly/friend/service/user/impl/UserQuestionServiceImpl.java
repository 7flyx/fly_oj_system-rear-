package com.fly.friend.service.user.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.fly.api.RemoteJudgeService;
import com.fly.api.domain.UserExeResult;
import com.fly.api.domain.dto.JudgeSubmitDTO;
import com.fly.api.domain.vo.UserQuestionResultVO;
import com.fly.common.core.constants.Constants;
import com.fly.common.core.domain.R;
import com.fly.common.core.enums.ProgramType;
import com.fly.common.core.enums.QuestionResType;
import com.fly.common.core.enums.ResultCode;
import com.fly.common.core.utils.ThreadLocalUtil;
import com.fly.common.security.exception.ServiceException;
import com.fly.friend.domain.question.Question;
import com.fly.friend.domain.question.es.QuestionCase;
import com.fly.friend.domain.question.es.QuestionES;
import com.fly.friend.domain.user.UserSubmit;
import com.fly.friend.domain.user.dto.UserSubmitDTO;
import com.fly.friend.elasticsearch.QuestionRepository;
import com.fly.friend.mapper.question.QuestionMapper;
import com.fly.friend.mapper.user.UserSubmitMapper;
import com.fly.friend.rabbit.JudgeProducer;
import com.fly.friend.service.user.IUserQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserQuestionServiceImpl implements IUserQuestionService {
    
    
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private RemoteJudgeService remoteJudgeService;

    @Autowired
    private JudgeProducer judgeProducer;

    @Autowired
    private UserSubmitMapper userSubmitMapper;
    
    @Override
    public R<UserQuestionResultVO> submit(UserSubmitDTO submitDTO) {
        Integer programType = submitDTO.getProgramType();
        if (ProgramType.JAVA.getValue().equals(programType)) { // 0: java
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTP(submitDTO);
            return remoteJudgeService.doJudgeJavaCode(judgeSubmitDTO); // openfeign 远程调用judge服务
        } else if (ProgramType.CPP.getValue().equals(programType)) { // 1:cpp

        } else if (ProgramType.PYTHON3.getValue().equals(programType)) { // 2: python3

        }
        throw new ServiceException(ResultCode.FAILED_NOT_SUPPORT_PROGRAM);
    }

    @Override
    public boolean rabbitSubmit(UserSubmitDTO submitDTO) {
        Integer programType = submitDTO.getProgramType();
        if (ProgramType.JAVA.getValue().equals(programType)) { // 0: java
            JudgeSubmitDTO judgeSubmitDTO = assembleJudgeSubmitDTP(submitDTO);
//            return remoteJudgeService.doJudgeJavaCode(judgeSubmitDTO); // openfeign 远程调用judge服务
            judgeProducer.produceMsg(judgeSubmitDTO); // 将数据塞进 消息队列中，那边会监听这个消息队列 进行消费
            return true;
        } else if (ProgramType.CPP.getValue().equals(programType)) { // 1:cpp

        } else if (ProgramType.PYTHON3.getValue().equals(programType)) { // 2: python3

        }
        throw new ServiceException(ResultCode.FAILED_NOT_SUPPORT_PROGRAM);
    }

    @Override
    public UserQuestionResultVO exeResult(Long examId, Long questionId, String currentTime) {
        Long userId = ThreadLocalUtil.get(Constants.USER_ID, Long.class);
        UserSubmit userSubmit = userSubmitMapper.selectCurrentUserSubmit(userId, examId, questionId, currentTime);
        UserQuestionResultVO resultVO = new UserQuestionResultVO();
        if (userSubmit == null) {
            resultVO.setPass(QuestionResType.IN_JUDGE.getValue());
        } else {
            resultVO.setPass(userSubmit.getPass());
            resultVO.setExeMessage(userSubmit.getExeMessage());
            if (StrUtil.isNotEmpty(userSubmit.getCaseJudgeRes())) {
                resultVO.setUserExeResultList(JSON.parseArray(userSubmit.getCaseJudgeRes(), UserExeResult.class));
            }
        }
        return resultVO;
    }

    private JudgeSubmitDTO assembleJudgeSubmitDTP(UserSubmitDTO submitDTO) {
        Long questionId = submitDTO.getQuestionId();
        QuestionES questionES = questionRepository.findById(questionId).orElse(null);
        JudgeSubmitDTO judgeSubmitDTO = new JudgeSubmitDTO();
        if (questionES != null) {
            BeanUtil.copyProperties(questionES, judgeSubmitDTO);
        } else {
            Question question = questionMapper.selectById(questionId);
            BeanUtil.copyProperties(question, judgeSubmitDTO);
            questionES = new QuestionES();
            BeanUtil.copyProperties(question, questionES);
            questionRepository.save(questionES);
        }
        judgeSubmitDTO.setUserId(ThreadLocalUtil.get(Constants.USER_ID, Long.class));
        judgeSubmitDTO.setExamId(submitDTO.getExamId());
        judgeSubmitDTO.setProgramType(submitDTO.getProgramType());
        judgeSubmitDTO.setUserCode(codeConnect(submitDTO.getUserCode(), questionES.getMainFunc()));
        // 测试用例
        List<QuestionCase> questionCaseList = JSONUtil.toList(questionES.getQuestionCase(), QuestionCase.class);
        List<String> inputList = questionCaseList.stream().map(QuestionCase::getInput).toList();
        List<String> outputList = questionCaseList.stream().map(QuestionCase::getOutput).toList();
        judgeSubmitDTO.setInputList(inputList);
        judgeSubmitDTO.setOutputList(outputList);
        return judgeSubmitDTO;
    }

    private String codeConnect(String userCode, String mainFunc) {
        String targetCharacter = "}";
        int targetLastIndex = userCode.lastIndexOf(targetCharacter);
        if (targetLastIndex != -1) {
            return userCode.substring(0, targetLastIndex) + "\n" + mainFunc + "\n" + userCode.substring(targetLastIndex);
        }
        throw new ServiceException(ResultCode.FAILED);
    }
}
