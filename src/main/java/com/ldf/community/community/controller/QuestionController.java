package com.ldf.community.community.controller;

import com.ldf.community.community.dto.CommentsDTO;
import com.ldf.community.community.dto.QuestionDTO;
import com.ldf.community.community.service.CommentService;
import com.ldf.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 问题详情
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable("id") Long id,
                           Model model) {
        QuestionDTO questionDTO = questionService.getDetailById(id);
        // 获取页面评论列表
        List<CommentsDTO> comments = commentService.listByQuestionId(id);

        // 增加浏览数
        questionService.incView(id);
        model.addAttribute("questionDTO", questionDTO);
        model.addAttribute("comments", comments);
        return "question";
    }

}
