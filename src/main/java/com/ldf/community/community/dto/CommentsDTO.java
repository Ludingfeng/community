package com.ldf.community.community.dto;

import com.ldf.community.community.model.User;
import lombok.Data;

/**
 * 评论列表数据封装
 */
@Data
public class CommentsDTO {
    private Long id;
    private Long parentId;
    private Integer type;
    private Long commentator;
    private Long gmtCreate;
    private Long gmtModified;
    private Long likeCount;
    private String description;
    private User user;
}
