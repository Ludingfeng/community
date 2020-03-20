package com.ldf.community.community.controller;

import com.ldf.community.community.dto.CommentDTO;
import com.ldf.community.community.dto.CommentsDTO;
import com.ldf.community.community.dto.ResultDTO;
import com.ldf.community.community.enums.CommentTypeEnum;
import com.ldf.community.community.exception.CustomizeErrorCode;
import com.ldf.community.community.model.Comment;
import com.ldf.community.community.model.User;
import com.ldf.community.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 评论功能
 */
@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    //评论
    @ResponseBody
    @RequestMapping(value = "/comment", method = RequestMethod.POST)
    public Object post(@RequestBody CommentDTO commentDTO,
                       HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return ResultDTO.errorOf(CustomizeErrorCode.NO_LOGIN);
        }
        if(commentDTO==null || StringUtils.isEmpty(commentDTO.getDescription().trim())){
            return ResultDTO.errorOf(CustomizeErrorCode.COMMENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setDescription(commentDTO.getDescription());
        comment.setParentId(commentDTO.getParentId());
        comment.setType(commentDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setLikeCount(0L);
        comment.setCommentator(user.getId());
        commentService.insert(comment,user);
        return ResultDTO.successOf();
    }

    // 获取二级评论列表
    @ResponseBody
    @RequestMapping(value = "/comment/{id}", method = RequestMethod.GET)
    public ResultDTO<List<CommentsDTO>> comments(@PathVariable("id")Long id){
        List<CommentsDTO> list = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.successOf(list);
    }

}
