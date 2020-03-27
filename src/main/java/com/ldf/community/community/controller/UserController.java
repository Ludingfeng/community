package com.ldf.community.community.controller;

import com.ldf.community.community.exception.CustomizeErrorCode;
import com.ldf.community.community.exception.CustomizeException;
import com.ldf.community.community.mapper.UserMapper;
import com.ldf.community.community.model.User;
import com.ldf.community.community.model.UserExample;
import com.ldf.community.community.service.NotificationService;
import com.ldf.community.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

/**
 * 用户相关功能
 */
@Controller
public class UserController {

    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/user/{action}")
    public String login(@PathVariable(name = "action") String action,
                        Model model) {
        if ("login".equals(action.trim())) {
            model.addAttribute("section", "login");
        } else if ("register".equals(action.trim())) {
            model.addAttribute("section", "register");
        }
        return "register";
    }

    /**
     * 登录
     *
     * @return
     */
    @RequestMapping("/userLogin")
    public String login(@RequestParam(value = "username") String username,
                        @RequestParam(value = "password") String password,
                        Model model,
                        HttpServletResponse response) {
        //根据username查询用户是否存在
        List<User> users = getUsers(username, password, model);
        //用户不存在
        if (users.size() <= 0 || users == null) {
            model.addAttribute("section", "login");
            model.addAttribute("errorMessage", "用户名不存在！");
            return "register";
        }
        //密码错误
        User user = users.get(0);
        if (!password.equals(user.getPassword().trim())) {
            model.addAttribute("section", "login");
            model.addAttribute("errorMessage", "密码错误！");
            return "register";
        }
        String token = UUID.randomUUID().toString();
        user.setToken(token);
        userService.creatOrUpdate(user);
        response.addCookie(new Cookie("token", token));
        return "redirect:/";
    }

    /**
     * 注册
     * @return
     */
    @RequestMapping("/userRegister")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           Model model) {
        //根据username查询用户是否存在
        List<User> users = getUsers(username, password, model);
        //用户已经存在，不允许注册
        if (users.size() > 0 && users != null) {
            model.addAttribute("section", "register");
            model.addAttribute("errorMessage", "用户名已存在！");
            return "register";
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setAccountId(UUID.randomUUID().toString());
        user.setGmtCreate(System.currentTimeMillis());
        user.setAvatarUrl("/images/default_avatar.png");
        int result = userMapper.insertSelective(user);
        if (result <= 0) {
            throw new CustomizeException(CustomizeErrorCode.REGISTER_FAIL);
        }
        return "redirect:/";
    }

    private List<User> getUsers(String username, String password, Model model) {
        UserExample userExample = new UserExample();
        userExample.createCriteria().andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(userExample);
        model.addAttribute("username", username);
        model.addAttribute("password", password);
        return users;
    }

}
