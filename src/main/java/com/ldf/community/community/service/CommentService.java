package com.ldf.community.community.service;

import com.ldf.community.community.dto.CommentsDTO;
import com.ldf.community.community.enums.CommentTypeEnum;
import com.ldf.community.community.exception.CustomizeErrorCode;
import com.ldf.community.community.exception.CustomizeException;
import com.ldf.community.community.mapper.CommentMapper;
import com.ldf.community.community.mapper.QuestionExtMapper;
import com.ldf.community.community.mapper.QuestionMapper;
import com.ldf.community.community.mapper.UserMapper;
import com.ldf.community.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 评论功能
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;
    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    public void insert(Comment comment) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            // 回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            // 回复的评论不存在
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
        } else {
            // 回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            // 回复的问题不存在
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            // 问题的回复数+1
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
        }
    }

    // 获取一级二级评论列表数据
    public List<CommentsDTO> listByTargetId(Long id, CommentTypeEnum type) {

        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andParentIdEqualTo(id)
                .andTypeEqualTo(type.getType());
        // 评论按点赞数降序，评论时间降序排列
        commentExample.setOrderByClause("like_count desc,gmt_create desc");
        List<Comment> comments = commentMapper.selectByExample(commentExample);
        if (comments.size() == 0) {
            return new ArrayList<>();
        }
        // 获取去重的评论人
        Set<Long> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> commentatorIds = new ArrayList<>();
        commentatorIds.addAll(commentators);
        // 获取评论人并转换为map
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(commentatorIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));
        // 转换comment为commentDTO
        List<CommentsDTO> commentDTOS = comments.stream().map(comment -> {
            CommentsDTO commentsDTO = new CommentsDTO();
            BeanUtils.copyProperties(comment,commentsDTO);
            commentsDTO.setUser(userMap.get(comment.getCommentator()));
            return commentsDTO;
        }).collect(Collectors.toList());

        return commentDTOS;
    }
}
