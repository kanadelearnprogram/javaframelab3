package com.space.controller;

import com.space.model.entity.User;
import com.space.service.UserService;
import com.space.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {
    
    UserService userService = new UserServiceImpl();//草泥马不许动就这么写
    
    @GetMapping("/login")
    public String showLogin(HttpServletRequest request) {
        // 检查用户是否已经登录
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user != null) {
            // 如果已登录，重定向到用户首页
            return "redirect:/home";
        }
        return "user/login";
    }

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
            // 登录成功后跳转到用户首页
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("error", "登录异常：" + e.getMessage());
            return "user/login";
        }
    }
    
    @GetMapping("/register")
    public String showRegister(HttpServletRequest request) {
        // 检查用户是否已经登录
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user != null) {
            // 如果已登录，重定向到用户首页
            return "redirect:/home";
        }
        return "user/register";
    }

    // 用户注册处理
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

    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("loginUser");
        return "redirect:/";
    }
    
    // 在用户首页展示 usedSize/totalSize
    @GetMapping("/size")
    public String showSize(HttpServletRequest request, Model model){
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        Long userId = user.getUserId();
        // TODO: 添加实际的空间大小查询逻辑
        model.addAttribute("userId", userId);
        return "user/space";
    }

}