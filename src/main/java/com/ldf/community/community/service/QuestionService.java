package com.ldf.community.community.service;

import com.ldf.community.community.dto.PaginationDTO;
import com.ldf.community.community.dto.QuestionDTO;
import com.ldf.community.community.dto.QuestionQueryDTO;
import com.ldf.community.community.exception.CustomizeErrorCode;
import com.ldf.community.community.exception.CustomizeException;
import com.ldf.community.community.mapper.QuestionExtMapper;
import com.ldf.community.community.mapper.QuestionMapper;
import com.ldf.community.community.mapper.UserMapper;
import com.ldf.community.community.model.Question;
import com.ldf.community.community.model.QuestionExample;
import com.ldf.community.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;

    // 首页获取数据分页
    public PaginationDTO list(String searchCon, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        if (!StringUtils.isEmpty(searchCon)) {
            String search = searchCon.replace(" ", "|");
            questionQueryDTO.setSearch(search);
        }
        //获取首页数据总条数
        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        Integer totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;//总页数

        if (page < 1) {
            page = 1;
        }
        if (totalPage != 0 && page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);
//        QuestionExample questionExample = new QuestionExample();
//        questionExample.setOrderByClause("gmt_create desc");
        questionQueryDTO.setSize(size);
        questionQueryDTO.setPage(offset);
        List<Question> list = questionExtMapper.selectBySearch(questionQueryDTO);
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : list) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    // 我的提问页获取数据分页
    public PaginationDTO list(Long userId, Integer page, Integer size) {
        PaginationDTO paginationDTO = new PaginationDTO();
        QuestionExample example = new QuestionExample();
        // 传入当前用户
        example.createCriteria().andCreatorEqualTo(userId);
        //获取我的提问页数据总条数
        Integer totalCount = (int) questionMapper.countByExample(example);

        Integer totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;//总页数

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        List<Question> list = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));
        List<QuestionDTO> questionDTOList = new ArrayList<>();
        for (Question question : list) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        return paginationDTO;
    }

    // 获取问题详细信息
    public QuestionDTO getDetailById(Long id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        // 获取问题创建者
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setUser(user);
        // 将question信息复制到questionDTO
        BeanUtils.copyProperties(question, questionDTO);
        return questionDTO;
    }

    // 更新或创建问题
    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            // 创建问题
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0);
            question.setViewCount(0);
            question.setLikeCount(0);
            questionMapper.insert(question);
        } else {
            // 更新问题
            question.setGmtModified(System.currentTimeMillis());
            int result = questionMapper.updateByPrimaryKeySelective(question);
            // 未更新成功，问题不存在
            if (result != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    // 增加浏览数
    public void incView(Long id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    // 通过标签获取相关问题列表
    public List<QuestionDTO> selectRelatedQuestions(QuestionDTO queryDTO) {

        if (StringUtils.isEmpty(queryDTO.getTag().trim())) {
            return new ArrayList<>();
        }
        String questionTags = queryDTO.getTag().replace(",", "|");
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(questionTags);
        List<Question> relatedQuestions = questionExtMapper.selectRelatedQuestions(question);
        List<QuestionDTO> relatedQuestionDTOs = relatedQuestions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return relatedQuestionDTOs;
    }
}
