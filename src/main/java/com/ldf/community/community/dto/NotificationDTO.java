package com.ldf.community.community.dto;

import com.ldf.community.community.model.Notification;
import lombok.Data;

import java.util.List;

@Data
public class NotificationDTO {

    List<Notification> notifications;
    private String questionTitle;

}
