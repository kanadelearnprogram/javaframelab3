package com.space.controller;

import com.space.model.entity.User;
import com.space.service.UserService;
import com.space.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;

@Controller("/user")
public class UserController {
    UserService userService = new UserServiceImpl();

    @PostMapping("/login")
    public String login(@RequestParam("account") String account,
                        @RequestParam("password") String password,
                        HttpServletRequest request,
                        Model model){
        try {
            User user = userService.login(account, password);
            if (user == null){
                model.addAttribute("error", "登录失败，用户名或密码错误");
                return "user/login";
            }
            request.getSession().setAttribute("loginUser",user);
            return "redirect:/index"; // 登录成功后跳转到主页
        } catch (Exception e) {
            model.addAttribute("error", "登录异常：" + e.getMessage());
            return "user/login";
        }
    }
    // 用户注册处理 - 支持日期类型绑定
    @PostMapping("/register")
    public String register(@RequestParam("account") String account,
                           @RequestParam("password") String password,
                           @RequestParam("nickname") String nickname,
                           Model model) {

        try {
            User user = new User();
            user.setAccount(account);
            user.setPassword(password); // 实际应用中应进行加密
            user.setNickname(nickname);
            user.setRole("user");
            user.setStatus(1); // 正常状态

            // 调用服务层注册用户，同时初始化用户空间
            boolean success = userService.registerUser(user);

            if (success) {
                model.addAttribute("message", "注册成功！请登录");
                return "redirect:/user/login";
            } else {
                model.addAttribute("error", "注册失败，请重试");
                return "user/register";
            }
        } catch (Exception e) {
            model.addAttribute("error", "注册异常：" + e.getMessage());
            return "user/register";
        }
    }
}