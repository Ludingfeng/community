package com.ldf.community.community.service;

import com.ldf.community.community.dto.NotificationDTO;
import com.ldf.community.community.dto.PaginationDTO;
import com.ldf.community.community.enums.NotificationTypeEnum;
import com.ldf.community.community.exception.CustomizeErrorCode;
import com.ldf.community.community.exception.CustomizeException;
import com.ldf.community.community.mapper.CommentMapper;
import com.ldf.community.community.mapper.NotificationMapper;
import com.ldf.community.community.model.Comment;
import com.ldf.community.community.model.Notification;
import com.ldf.community.community.model.NotificationExample;
import com.ldf.community.community.model.User;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private CommentMapper commentMapper;

    // 获取最新回复列表
    public PaginationDTO list(Long userId, Integer page, Integer size, Integer readStatus) {
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<>();
        NotificationExample notificationExample = new NotificationExample();
        // 传入当前用户
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        //获取我的提问页数据总条数
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);

        Integer totalPage = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;//总页数

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        paginationDTO.setPagination(totalPage, page);

        Integer offset = size * (page - 1);
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(readStatus);
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(example, new RowBounds(offset, size));

        if (notifications.size() == 0) {
            return paginationDTO;
        }

        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setType(NotificationTypeEnum.getNameByType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    // 获取通知的数量
    public Long count(Long userId) {
        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId).andStatusEqualTo(0);
        long count = notificationMapper.countByExample(example);
        return count;
    }

    // 读取未读消息
    public Notification readNotification(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        // 防止手动输入url读取别人消息
        if (notification.getReceiver() != user.getId()) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_READ_FAIL);
        }
        // 将未读消息变为已读 status改为1
        notification.setStatus(1);
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andIdEqualTo(id).andReceiverEqualTo(user.getId());
        int result = notificationMapper.updateByExampleSelective(notification, notificationExample);
        if (result != 1) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_READ_FAIL);
        }
        return notification;
    }

    // 通过二级评论id获取到问题id
    public Comment getParentIdByOuterId(Long outerId) {
        Comment comment = commentMapper.selectByPrimaryKey(outerId);
        if (comment == null) {
            throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
        }
        return comment;
    }
}
