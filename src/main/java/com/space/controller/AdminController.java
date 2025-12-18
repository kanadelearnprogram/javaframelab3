package com.space.controller;

import com.space.model.entity.Files;
import com.space.model.entity.User;
import com.space.service.FileService;
import com.space.service.UserService;
import com.space.service.impl.FileServiceImpl;
import com.space.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService = new UserServiceImpl();
    private final FileService fileService = new FileServiceImpl();

    // 检查用户是否为管理员的拦截器方法
    private boolean isAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("loginUser");
        return user != null && "admin".equals(user.getRole());
    }

    // 管理员首页 - 显示待审核文件列表
    @GetMapping("/dashboard")
    public String adminDashboard(HttpServletRequest request, Model model) {
        // 检查用户权限
        if (!isAdmin(request)) {
            return "redirect:/user/login";
        }

        User loginUser = (User) request.getSession().getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        // 获取待审核文件列表
        List<Files> pendingFiles = fileService.listPendingFiles();
        model.addAttribute("pendingFiles", pendingFiles);

        return "admin/dashboard";
    }

    // 审核文件操作
    @PostMapping("/review")
    public String reviewFile(@RequestParam("fileId") Long fileId,
                             @RequestParam("action") String action,
                             @RequestParam(value = "reason", required = false) String reason,
                             HttpServletRequest request,
                             Model model) {
        // 检查用户权限
        if (!isAdmin(request)) {
            return "redirect:/user/login";
        }

        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        try {
            // 根据操作类型处理文件审核
            if ("approve".equals(action)) {
                fileService.approveFile(fileId, loginUser.getUserId());
                model.addAttribute("message", "文件审核通过成功");
            } else if ("reject".equals(action)) {
                fileService.rejectFile(fileId, loginUser.getUserId(), reason);
                model.addAttribute("message", "文件审核驳回成功");
            }
        } catch (Exception e) {
            model.addAttribute("error", "审核操作失败: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }
}