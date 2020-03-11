package com.ldf.community.community.dto;

import lombok.Data;

/**
 * 封装传给服务器后台的评论对象
 */
@Data
public class CommentDTO {

    private Long parentId;
    private String description;
    private Integer type;

}
