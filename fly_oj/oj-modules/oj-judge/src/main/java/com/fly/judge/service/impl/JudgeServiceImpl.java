package com.fly.judge.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fly.api.domain.UserExeResult;
import com.fly.api.domain.dto.JudgeSubmitDTO;
import com.fly.api.domain.vo.UserQuestionResultVO;
import com.fly.common.core.constants.Constants;
import com.fly.common.core.enums.CodeRunStatus;
import com.fly.common.core.enums.JudgeConstants;
import com.fly.judge.domain.SandBoxExecuteResult;
import com.fly.judge.domain.UserSubmit;
import com.fly.judge.mapper.UserSubmitMapper;
import com.fly.judge.service.IJudgeService;
import com.fly.judge.service.ISandboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JudgeServiceImpl implements IJudgeService {

    @Autowired
    private ISandboxService sandboxService;

    @Autowired
    private UserSubmitMapper userSubmitMapper;

    @Override
    public UserQuestionResultVO doJudgeJavaCode(JudgeSubmitDTO judgeSubmitDTO) {
        SandBoxExecuteResult result =
                sandboxService.exeJavaCode(judgeSubmitDTO.getUserId(), judgeSubmitDTO.getUserCode(), judgeSubmitDTO.getInputList());
        UserQuestionResultVO userQuestionResultVO = new UserQuestionResultVO();
        if (result != null && CodeRunStatus.SUCCEED.equals(result.getRunStatus())) { // 运行成功
            // 比对执行结果，时间限制 空间限制
            doJudge(judgeSubmitDTO, result, userQuestionResultVO);
        } else { // 运行失败
            userQuestionResultVO.setPass(Constants.FALSE);
            if (result.getExeMessage() != null) {
                userQuestionResultVO.setExeMessage(result.getExeMessage());
            } else {
                userQuestionResultVO.setExeMessage(CodeRunStatus.UNKNOWN_FAILED.getMsg());
            }
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE);
        }
        saveUserSubmit(judgeSubmitDTO, userQuestionResultVO);
        return userQuestionResultVO;
    }

    private void doJudge(JudgeSubmitDTO judgeSubmitDTO,
                         SandBoxExecuteResult result,
                         UserQuestionResultVO userQuestionResultVO) {
        List<String> exeOutputList = result.getOutputList(); // 实际执行的输出结果
        List<String> outputList = judgeSubmitDTO.getOutputList(); // 预期的执行结果
        if (exeOutputList.size() != outputList.size()) {
            userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE); // 成绩
            userQuestionResultVO.setPass(Constants.FALSE); // 是否通过
            userQuestionResultVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg()); // 没有通过用例
        } else {
            boolean passed = true;
            List<UserExeResult> list = new ArrayList<>(); // 没通过的测试用例
            for (int i = 0; i < outputList.size(); i++) {
                String output = outputList.get(i);
                String exeOutput = exeOutputList.get(i);

                if (!output.equals(exeOutput)) { // 将测试没通过的测试用例，返回
                    String input = judgeSubmitDTO.getInputList().get(i);
                    passed = false;
                    UserExeResult tmp = new UserExeResult();
                    tmp.setOutput(output);
                    tmp.setInput(input);
                    tmp.setExeOutput(exeOutput);
                    userQuestionResultVO.setUserExeResultList(list);
                    break;
                }
            }
            if(!passed) { // 没有通过测试用例
                userQuestionResultVO.setPass(Constants.FALSE); // 是否通过
                userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE); // 成绩
                userQuestionResultVO.setExeMessage(CodeRunStatus.NOT_ALL_PASSED.getMsg()); // 没有通过用例
            } else if(result.getUseMemory() > judgeSubmitDTO.getSpaceLimit()) { // 超过空间限制
                userQuestionResultVO.setPass(Constants.FALSE); // 是否通过
                userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE); // 成绩
                userQuestionResultVO.setExeMessage(CodeRunStatus.OUT_OF_MEMORY.getMsg());
            } else if(result.getUseTime() > judgeSubmitDTO.getTimeLimit()) { // 超过时间限制
                userQuestionResultVO.setPass(Constants.FALSE); // 是否通过
                userQuestionResultVO.setScore(JudgeConstants.ERROR_SCORE); // 成绩
                userQuestionResultVO.setExeMessage(CodeRunStatus.OUT_OF_TIME.getMsg());
            } else {
                userQuestionResultVO.setPass(Constants.TRUE);
            }
            int score = judgeSubmitDTO.getDifficulty() * JudgeConstants.DEFAULT_SCORE; // 难度*成绩
            userQuestionResultVO.setScore(score);
        }
    }

    private void saveUserSubmit(JudgeSubmitDTO judgeSubmitDTO, UserQuestionResultVO userQuestionResultVO) {
        UserSubmit userSubmit = new UserSubmit();
        BeanUtil.copyProperties(userQuestionResultVO, userSubmit);

        userSubmit.setUserId(judgeSubmitDTO.getUserId());
        userSubmit.setUserCode(judgeSubmitDTO.getUserCode());
        userSubmit.setQuestionId(judgeSubmitDTO.getQuestionId());
        userSubmit.setExamId(judgeSubmitDTO.getExamId());
        userSubmit.setProgramType(judgeSubmitDTO.getProgramType());
        userSubmit.setCreateBy(judgeSubmitDTO.getUserId());
        // 先删除上一次提交的代码记录
        userSubmitMapper.delete(new LambdaQueryWrapper<UserSubmit>()
                .eq(UserSubmit::getUserId, judgeSubmitDTO.getUserCode())
                .eq(UserSubmit::getQuestionId, judgeSubmitDTO.getQuestionId())
                        .isNull(judgeSubmitDTO.getExamId() == null, UserSubmit::getExamId)
                .eq(judgeSubmitDTO.getExamId() != null, UserSubmit::getExamId, judgeSubmitDTO.getExamId()));
        // 插入数据库
        userSubmitMapper.insert(userSubmit);
    }
}
