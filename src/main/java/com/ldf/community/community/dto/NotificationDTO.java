package com.ldf.community.community.dto;

import lombok.Data;

@Data
public class NotificationDTO {

    private Long id;
    private Long gmtCreate;
    private Integer status;
    private Long notifier;
    private String notifierName;
    // 回复的问题标题
    private String outerTitle;
    // NotificationTypeEnum中的name
    private String type;

}
