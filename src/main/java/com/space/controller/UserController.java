package com.space.controller;

import com.space.model.entity.Space;
import com.space.model.entity.User;
import com.space.service.SpaceService;
import com.space.service.UserService;
import com.space.service.impl.SpaceServiceImpl;
import com.space.service.impl.UserServiceImpl;
import com.space.service.FileService;
import com.space.service.impl.FileServiceImpl;
import com.space.model.entity.Files;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    
    UserService userService = new UserServiceImpl();//草泥马不许动就这么写
    FileService fileService = new FileServiceImpl();
    SpaceService spaceService = new SpaceServiceImpl();
    
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
    public String showSize(HttpServletRequest request, Model model,
                          @RequestParam(defaultValue = "1") int pageNum,
                          @RequestParam(defaultValue = "5") int pageSize){
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        Long userId = user.getUserId();
        
        // 获取用户空间信息
        Long usedSize = spaceService.findUsedSizeById(userId);
        Long totalSize = spaceService.findTotalSize(userId);
        double usedSizePercent = totalSize > 0 ? (usedSize * 100.0) / totalSize : 0;
        
        // 获取用户空间详细信息（包含申请状态）
        Space space = spaceService.findByUserId(userId);
        
        // 获取用户已上传的文件列表（带分页）
        List<Files> uploadedFiles = fileService.listFileWithPagination(userId, pageNum, pageSize);
        
        // 获取用户置顶文件列表
        List<Files> toppedFiles = fileService.listTopFiles(userId);
        
        // 获取用户图片列表用于轮播
        List<String> imgPathList = fileService.listImg(userId);
        
        model.addAttribute("uploadedFiles", uploadedFiles);
        model.addAttribute("toppedFiles", toppedFiles);
        model.addAttribute("imgPathList", imgPathList);
        model.addAttribute("userId", userId);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("usedSize", usedSize);
        model.addAttribute("totalSize", totalSize);
        model.addAttribute("usedSizePercent", usedSizePercent); // 传递double值而不是字符串
        model.addAttribute("space", space); // 添加空间详细信息
        return "user/space";
    }

    // 空间管理
    @PostMapping("/addsize")
    public String addSpaceSize(HttpServletRequest request, Model model){
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }

        Long userId = loginUser.getUserId();
        Long currentTotalSize = spaceService.findTotalSize(userId);
        Long newTotalSize = currentTotalSize + 100 * 1024 * 1024L; // 增加100MB

        Boolean result = spaceService.updateTotalSize(userId, newTotalSize);
        // todo 审核
        if (result) {
            model.addAttribute("message", "空间扩容成功！您已增加100MB存储空间。");
        } else {
            model.addAttribute("error", "空间扩容失败，请稍后重试。");
        }

        // 重新加载空间信息
        return "redirect:/user/size";
    }
    
    // 提交空间扩容申请
    @PostMapping("/apply-expansion")
    public String applyExpansion(@RequestParam("expansionSize") Long expansionSize,
                                 HttpServletRequest request,
                                 Model model) {
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login";
        }

        Long userId = loginUser.getUserId();
        // 将MB转换为字节
        Long applySize = expansionSize * 1024 * 1024L;

        try {
            Boolean result = spaceService.submitExpansionApplication(userId, applySize);
            if (result) {
                model.addAttribute("message", "空间扩容申请提交成功，请等待管理员审核。");
            } else {
                model.addAttribute("error", "空间扩容申请提交失败，请稍后重试。");
            }
        } catch (Exception e) {
            model.addAttribute("error", "空间扩容申请提交异常：" + e.getMessage());
        }

        // 重新加载空间信息
        return "redirect:/user/size";
    }
}