package com.space.controller;

import com.space.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 网站首页路由
    @GetMapping("/")
    public String index(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("loginUser");
        model.addAttribute("loginUser", user);
        return "index"; // 返回网站首页
    }

    // 用户首页路由
    @GetMapping("/home")
    public String home(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            // 如果未登录，重定向到登录页面
            return "redirect:/user/login";
        }
        model.addAttribute("loginUser", user);
        return "home"; // 返回用户首页
    }
}