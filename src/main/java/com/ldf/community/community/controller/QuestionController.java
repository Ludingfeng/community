package com.ldf.community.community.controller;

import com.ldf.community.community.dto.QuestionDTO;
import com.ldf.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 问题详情
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable("id") Integer id,
                           Model model) {
        QuestionDTO questionDTO = questionService.getDetailById(id);
        model.addAttribute("questionDTO",questionDTO);
        return "question";
    }

}
