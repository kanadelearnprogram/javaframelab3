package com.space.controller;

import com.space.model.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

@Controller
public class FilesController {
    //todo 文件上传下载
    
    @GetMapping("/upload")
    public String showUploadForm() {
        return "user/space";
    }
    
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, 
                         RedirectAttributes redirectAttributes,
                         HttpServletRequest request) throws IOException {
        
        // 检查用户是否登录
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "请选择文件");
            return "redirect:/user/size";
        }

        try {
            String originalFilename = file.getOriginalFilename();
            // 确保目录存在
            File uploadDir = new File("E:\\codelab\\javaframealab3\\src\\main\\resources\\files" + user.getNickname());
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            File objFile = new File(uploadDir, originalFilename);
            file.transferTo(objFile);
            redirectAttributes.addFlashAttribute("message", "文件上传成功: " + originalFilename);
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "文件上传失败: " + e.getMessage());
        }
        
        return "redirect:/user/size";
    }

}