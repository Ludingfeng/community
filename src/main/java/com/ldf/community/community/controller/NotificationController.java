package com.ldf.community.community.controller;

import com.ldf.community.community.model.Comment;
import com.ldf.community.community.model.Notification;
import com.ldf.community.community.model.User;
import com.ldf.community.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @RequestMapping("/notification/{id}")
    public String readNotification(@PathVariable("id") Long id,
                                   HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        // 读取未读消息
        Notification notification = notificationService.readNotification(id, user);
        if (notification.getType() == 1) {
            return "redirect:/question/" + notification.getOuterId();
        } else if (notification.getType() == 2) {
            Comment comment = notificationService.getParentIdByOuterId(notification.getOuterId());
            return "redirect:/question/" + comment.getParentId();
        }
        return "redirect:/";
    }

}
