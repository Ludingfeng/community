package com.ldf.community.community.controller;

import com.ldf.community.community.cache.TagCache;
import com.ldf.community.community.dto.QuestionDTO;
import com.ldf.community.community.model.Question;
import com.ldf.community.community.model.User;
import com.ldf.community.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    // 编辑问题
    @GetMapping("/publish/{id}")
    public String editProblem(@PathVariable("id") Long id,
                              Model model) {
        QuestionDTO question = questionService.getDetailById(id);
        model.addAttribute("title", question.getTitle());
        model.addAttribute("description", question.getDescription());
        model.addAttribute("tag", question.getTag());
        model.addAttribute("id", question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    // 发布问题
    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(
            @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "tag", required = false) String tag,
            @RequestParam(value = "id", required = false) Long id,
            HttpServletRequest request,
            Model model
    ) {
        model.addAttribute("title", title);
        model.addAttribute("description", description);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCache.get());

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            System.out.println("用户未登录");
            model.addAttribute("error", "用户未登录！");
            return "publish";
        }
        if (title == null || "".equals(title.trim())) {
            System.out.println("标题不能为空");
            model.addAttribute("error", "标题不能为空！");
            return "publish";
        }
        if (description == null || "".equals(description.trim())) {
            System.out.println("问题补充不能为空");
            model.addAttribute("error", "问题补充不能为空！");
            return "publish";
        }
        if (tag == null || "".equals(tag.trim())) {
            System.out.println("标签不能为空");
            model.addAttribute("error", "标签不能为空！");
            return "publish";
        }
        String invalid = TagCache.filterInvalid(tag);
        if(!StringUtils.isEmpty(invalid)){
            model.addAttribute("error", "输入非法标签："+invalid);
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setTag(tag);
        question.setDescription(description);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";

    }

}
