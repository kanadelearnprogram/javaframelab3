package com.space.controller;

import com.space.mapper.FileMapper;
import com.space.mapper.SpaceMapper;
import com.space.model.entity.Files;
import com.space.model.entity.User;
import com.space.service.FileService;
import com.space.service.SpaceService;
import com.space.service.impl.FileServiceImpl;
import com.space.service.impl.SpaceServiceImpl;
import com.space.util.MyBatisUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@Controller
public class FilesController {

    FileService fileService = new FileServiceImpl();
    SpaceService spaceService = new SpaceServiceImpl();
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
            return "redirect:/upload";
        }

        try {
            // 检查空间大小
            SqlSession sqlSession = MyBatisUtil.getSession();
            SpaceMapper spaceMapper = sqlSession.getMapper(SpaceMapper.class);
            Long totalSize = spaceMapper.selectSpaceTotalSize(user.getUserId());
            Long usedSize = spaceMapper.selectUsedSize(user.getUserId());
            sqlSession.close();

            long size = file.getSize();
            if (!(totalSize > usedSize + size)) {
                redirectAttributes.addFlashAttribute("message", "文件过大或剩余空间不足");
                return "redirect:/upload";
            }

            String originalFilename = file.getOriginalFilename();
            // 确保目录存在
            File uploadDir = new File("E:\\codelab\\javaframealab3\\files\\" + user.getNickname());
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            File objFile = new File(uploadDir, originalFilename);
            file.transferTo(objFile);
            // 保存到数据库
            Files files = new Files();
            files.setUserId(user.getUserId());
            files.setFileName(originalFilename);
            files.setFilePath(String.valueOf(uploadDir));
            files.setFileSize(size);
            //todo 匹配对应类型
            files.setFileType("other");
            files.setStatus("待审核");
            files.setIsTop(0); // 添加默认值，解决is_top不能为null的问题
            
            // 使用Service保存文件信息到数据库
            boolean success = fileService.saveFile(files);

            if (success) {
                redirectAttributes.addFlashAttribute("message", "文件上传成功: " + originalFilename);
            } else {
                redirectAttributes.addFlashAttribute("message", "文件保存到数据库失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "文件上传失败: " + e.getMessage());
        }

        return "redirect:/upload";
    }
    
    @GetMapping("/download/{filename}")
    public void download(@PathVariable String filename,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        // 检查用户是否登录
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "请先登录");
            return;
        }
        
        // 构建文件路径
        File uploadDir = new File("E:\\codelab\\javaframealab3\\src\\main\\resources\\files\\" + user.getNickname());
        File file = new File(uploadDir, filename);
        
        // 检查文件是否存在
        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }
        
        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + 
                          URLEncoder.encode(filename, StandardCharsets.UTF_8.toString()));
        response.setContentLength((int) file.length());
        
        // 写入响应
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }
}