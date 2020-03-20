package com.ldf.community.community.service;

import com.ldf.community.community.dto.CommentsDTO;
import com.ldf.community.community.enums.CommentTypeEnum;
import com.ldf.community.community.enums.NotificationTypeEnum;
import com.ldf.community.community.exception.CustomizeErrorCode;
import com.ldf.community.community.exception.CustomizeException;
import com.ldf.community.community.mapper.*;
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
    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional
    public void insert(Comment comment, User commentator) {
        if (comment.getParentId() == null || comment.getParentId() == 0) {
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if (comment.getType() == null || !CommentTypeEnum.isExist(comment.getType())) {
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        // 回复评论
        if (comment.getType() == CommentTypeEnum.COMMENT.getType()) {
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            // 回复的评论不存在
            if (dbComment == null) {
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
            // 创建通知
            createNotify(comment, dbComment.getCommentator(), commentator.getName(), dbComment.getDescription(), NotificationTypeEnum.REPLY_COMMENT);
        // 回复问题
        } else {
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            // 回复的问题不存在
            if (question == null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            // 问题的回复数+1
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
            // 创建通知
            createNotify(comment, question.getCreator(),commentator.getName(), question.getTitle(), NotificationTypeEnum.REPLY_QUESTION);
        }
    }

    private void createNotify(Comment comment, Long receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationType) {
        Notification notification = new Notification();
        //设置回复人id
        notification.setNotifier(comment.getCommentator());
        //设置回复人名字
        notification.setNotifierName(notifierName);
        notification.setGmtCreate(System.currentTimeMillis());
        //设置未读
        notification.setStatus(0);
        //回复问题还是回复评论
        notification.setType(notificationType.getType());
        notification.setOuterId(comment.getParentId());
        //设置接收人
        notification.setReceiver(receiver);
        //回复的问题或评论的标题
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
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
