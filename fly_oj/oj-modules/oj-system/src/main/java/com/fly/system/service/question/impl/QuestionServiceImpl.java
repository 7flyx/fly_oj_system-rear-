package com.fly.system.service.question.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fly.common.core.constants.Constants;
import com.fly.common.core.enums.ResultCode;
import com.fly.common.security.exception.ServiceException;
import com.fly.system.domain.question.Question;
import com.fly.system.domain.question.QuestionES;
import com.fly.system.domain.question.dto.QuestionAddDTO;
import com.fly.system.domain.question.dto.QuestionQueryDTO;
import com.fly.system.domain.question.vo.QuestionDetailVO;
import com.fly.system.domain.question.vo.QuestionEditDTO;
import com.fly.system.domain.question.vo.QuestionVO;
import com.fly.system.elasticsearch.QuestionRepository;
import com.fly.system.manager.QuestionCacheManager;
import com.fly.system.mapper.question.QuestionMapper;
import com.fly.system.service.question.IQuestionService;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements IQuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionCacheManager questionCacheManager;

    @Override
    public List<QuestionVO> list(QuestionQueryDTO questionQueryDTO) {
        String excludeIdStr = questionQueryDTO.getExcludeIdStr();
        if (StrUtil.isNotEmpty(excludeIdStr)) {
            String[] excludeIdArr = excludeIdStr.split(Constants.SPLIT_SEM);
            Set<Long> collect = Arrays.stream(excludeIdArr)
                    .map(Long::valueOf)
                    .collect(Collectors.toSet()); // 将前端传输过来的字符串，进行分割 提取出 题目id，再转换成long型 存储在set中
            questionQueryDTO.setExcludeIdSet(collect);
        }
        // 分页之后的剩余的数据，不是总的数据量
        PageHelper.startPage(questionQueryDTO.getPageNum(), questionQueryDTO.getPageSize());
        List<QuestionVO> questionVOS = questionMapper.selectQuestionList(questionQueryDTO);
        return questionVOS;
    }

    @Override
    public boolean add(QuestionAddDTO questionAddDTO) {
        // 判断重复性，比如标题相同等，这里就不写了
//        questionMapper.selectList(new LambdaQueryWrapper<Question>())
        Question question = new Question();
        BeanUtil.copyProperties(questionAddDTO, question);
        int row = questionMapper.insert(question);
        if (row <= 0) {
            return false;
        }
        QuestionES questionES = new QuestionES();
        BeanUtil.copyProperties(question, questionES);
        questionRepository.save(questionES); // 往 ES中插入
        questionCacheManager.addCache(questionES.getQuestionId());
        return true;
    }

    @Override
    public QuestionDetailVO detail(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        QuestionDetailVO result = new QuestionDetailVO();
        BeanUtil.copyProperties(question, result);
        return result;
    }

    @Override
    public int edit(QuestionEditDTO questionEditDTO) {
        Question oldQuestion = questionMapper.selectById(questionEditDTO.getQuestionId());
        if (oldQuestion == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        oldQuestion.setTitle(questionEditDTO.getTitle());
        oldQuestion.setDifficulty(questionEditDTO.getDifficulty());
        oldQuestion.setTimeLimit(questionEditDTO.getTimeLimit());
        oldQuestion.setSpaceLimit(questionEditDTO.getSpaceLimit());
        oldQuestion.setContent(questionEditDTO.getContent());
        oldQuestion.setQuestionCase(questionEditDTO.getQuestionCase());
        oldQuestion.setDefaultCode(questionEditDTO.getDefaultCode());
        oldQuestion.setMainFunc(questionEditDTO.getMainFunc());

        QuestionES questionES = new QuestionES();
        BeanUtil.copyProperties(oldQuestion, questionES);
        questionRepository.save(questionES); // 往 ES中插入
        return questionMapper.updateById(oldQuestion);
    }

    @Override
    public int delete(Long questionId) {
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            throw new ServiceException(ResultCode.FAILED_NOT_EXISTS);
        }
        questionRepository.deleteById(questionId); // 在ES中 删除
        questionCacheManager.deleteCache(questionId);
        return questionMapper.deleteById(questionId);
    }
}
