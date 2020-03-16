package com.ldf.community.community.controller;

import com.ldf.community.community.dto.NotificationDTO;
import com.ldf.community.community.dto.PaginationDTO;
import com.ldf.community.community.model.User;
import com.ldf.community.community.service.NotificationService;
import com.ldf.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ProfileController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action") String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        User user = (User)request.getSession().getAttribute("user");

        if (user == null) {
            return "redirect:/";
        }
        // 我的提问
        if ("questions".equals(action)) {
            model.addAttribute("section", "questions");
            model.addAttribute("sectionName", "我的提问");
            // 获取提问页列表返回给页面
            PaginationDTO paginationDTO = questionService.list(user.getId(),page,size);
            model.addAttribute("paginationDTO", paginationDTO);
        // 最新回复
        } else if ("replies".equals(action)) {
            model.addAttribute("section", "replies");
            model.addAttribute("sectionName", "最新回复");
            NotificationDTO notificationDTO = notificationService.list();
            model.addAttribute("notificationDTO", notificationDTO);
        }
        return "profile";
    }

}
