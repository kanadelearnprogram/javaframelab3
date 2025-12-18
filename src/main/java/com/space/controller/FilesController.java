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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Controller
public class FilesController {

    FileService fileService = new FileServiceImpl();
    SpaceService spaceService = new SpaceServiceImpl();
    
    // 定义各种文件类型的扩展名
    private static final List<String> DOCUMENT_EXTENSIONS = Arrays.asList(
        "txt", "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "odt", "ods", "odp"
    );
    
    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList(
        "jpg", "jpeg", "png", "gif", "bmp", "svg", "webp", "tiff", "ico"
    );
    
    private static final List<String> AUDIO_EXTENSIONS = Arrays.asList(
        "mp3", "wav", "ogg", "flac", "aac", "wma", "m4a"
    );
    
    private static final List<String> VIDEO_EXTENSIONS = Arrays.asList(
        "mp4", "avi", "mkv", "mov", "wmv", "flv", "webm", "m4v"
    );
    
    // 根据文件扩展名判断文件类型
    private String getFileType(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "其他";
        }
        
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
        
        if (DOCUMENT_EXTENSIONS.contains(extension)) {
            return "文档";
        } else if (IMAGE_EXTENSIONS.contains(extension)) {
            return "图片";
        } else if (AUDIO_EXTENSIONS.contains(extension)) {
            return "音频";
        } else if (VIDEO_EXTENSIONS.contains(extension)) {
            return "视频";
        } else {
            return "其他";
        }
    }
    
    // 文件上传下载
    @GetMapping("/upload")
    public String showUploadForm(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }

        List<Files> uploadedFiles = fileService.listFile(user.getUserId());
        model.addAttribute("uploadedFiles", uploadedFiles);
        model.addAttribute("userId", user.getUserId());
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
            // 根据文件扩展名匹配对应类型
            files.setFileType(getFileType(originalFilename));
            files.setStatus(0);
            files.setIsTop(0); // 添加默认值，解决is_top不能为null的问题
            files.setUploadTime(LocalDateTime.now()); // 设置上传时间
            
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
    
    @GetMapping("/download/{fileId}")
    public void download(@PathVariable Long fileId,
                         HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        // 检查用户是否登录
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "请先登录");
            return;
        }
        
        // 从数据库获取文件信息
        SqlSession sqlSession = MyBatisUtil.getSession();
        FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
        Files file = fileMapper.findById(fileId.intValue());
        
        // 检查文件是否存在
        if (file == null) {
            sqlSession.close();
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }
        
        // 检查文件是否属于当前用户
        if (!file.getUserId().equals(user.getUserId())) {
            sqlSession.close();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权访问该文件");
            return;
        }
        
        // 增加下载次数（通过Service层处理）
        fileService.incrementDownloadCount(fileId);
        
        // 获取下载次数并打印到控制台（可根据需要删除）
        Integer downloadCount = fileService.getDownloadCount(fileId);
        System.out.println("文件 " + file.getFileName() + " 下载次数: " + downloadCount);
        
        sqlSession.close();
        
        // 构建文件路径
        File uploadDir = new File(file.getFilePath());
        File actualFile = new File(uploadDir, file.getFileName());
        
        // 检查文件是否存在
        if (!actualFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }
        
        // 设置响应头
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + 
                          URLEncoder.encode(file.getFileName(), StandardCharsets.UTF_8.toString()));
        response.setContentLength((int) actualFile.length());
        
        // 写入响应
        try (FileInputStream fis = new FileInputStream(actualFile);
             OutputStream os = response.getOutputStream()) {
            
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }
    
    // 文件预览功能
    @GetMapping("/preview/{fileId}")
    public void preview(@PathVariable Long fileId,
                        HttpServletRequest request,
                        HttpServletResponse response) throws IOException {
        // 检查用户是否登录
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "请先登录");
            return;
        }
        
        // 从数据库获取文件信息
        SqlSession sqlSession = MyBatisUtil.getSession();
        FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
        Files file = fileMapper.findById(fileId.intValue());
        sqlSession.close();
        
        // 检查文件是否存在
        if (file == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }
        
        // 检查文件是否属于当前用户
        /*if (!file.getUserId().equals(user.getUserId())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权访问该文件");
            return;
        }*/
        
        // 构建文件路径
        File uploadDir = new File(file.getFilePath());
        File actualFile = new File(uploadDir, file.getFileName());
        
        // 检查文件是否存在
        if (!actualFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
            return;
        }
        
        // 根据文件类型设置Content-Type并返回内容
        if ("图片".equals(file.getFileType())) {
            serveMediaFile(response, actualFile, file.getFileName(), "image");
        } else if ("音频".equals(file.getFileType())) {
            serveMediaFile(response, actualFile, file.getFileName(), "audio");
        } else if ("视频".equals(file.getFileType())) {
            serveMediaFile(response, actualFile, file.getFileName(), "video");
        } else {
            response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "不支持预览该文件类型");
        }
    }
    
    // 为媒体文件提供服务的辅助方法
    private void serveMediaFile(HttpServletResponse response, File file, String fileName, String mediaType) throws IOException {
        // 设置适当的Content-Type
        String contentType = "application/octet-stream";
        
        // 获取文件扩展名
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        switch (mediaType) {
            case "image":
                if ("jpg".equals(extension) || "jpeg".equals(extension)) {
                    contentType = "image/jpeg";
                } else if ("png".equals(extension)) {
                    contentType = "image/png";
                } else if ("gif".equals(extension)) {
                    contentType = "image/gif";
                } else {
                    contentType = "image/" + extension;
                }
                break;
            case "audio":
                if ("mp3".equals(extension)) {
                    contentType = "audio/mpeg";
                } else if ("wav".equals(extension)) {
                    contentType = "audio/wav";
                } else if ("ogg".equals(extension)) {
                    contentType = "audio/ogg";
                } else {
                    contentType = "audio/" + extension;
                }
                break;
            case "video":
                if ("mp4".equals(extension)) {
                    contentType = "video/mp4";
                } else if ("avi".equals(extension)) {
                    contentType = "video/x-msvideo";
                } else if ("mov".equals(extension)) {
                    contentType = "video/quicktime";
                } else {
                    contentType = "video/" + extension;
                }
                break;
        }
        
        System.out.println("Serving file: " + fileName + " with content type: " + contentType);
        
        response.setContentType(contentType);
        response.setContentLength((int) file.length());
        
        // 支持范围请求（用于视频流）
        response.setHeader("Accept-Ranges", "bytes");
        
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
    
    /*@GetMapping("/list")
    public String listFiles(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }

        List<Files> uploadedFiles = fileService.listFile(user.getUserId());
        model.addAttribute("uploadedFiles", uploadedFiles);
        model.addAttribute("userId", user.getUserId());
        return "user/space";
    }*/
    
    // 专门用于获取已上传文件列表的接口
    /*@GetMapping("/files")
    public String getUploadedFiles(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }

        List<Files> uploadedFiles = fileService.listFile(user.getUserId());
        model.addAttribute("uploadedFiles", uploadedFiles);
        return "user/space"; // 返回相同的视图，但数据来自不同的接口
    }
    */
    @PostMapping("/freeze")
    public String freezeFile(HttpServletRequest request, Long fileId, Model model){
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        fileService.freezeFile(fileId);
        return "redirect:/user/size";
    }
    @PostMapping("/unfreeze")
    public String unfreezeFile(HttpServletRequest request, Long fileId, Model model){
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        fileService.unfreezeFile(fileId);
        return "redirect:/user/size";
    }
    @PostMapping("/delete")
    public String deleteFile(HttpServletRequest request, Long fileId, Model model){
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        fileService.deleteFile(fileId);
        return "redirect:/user/size";
    }
    @PostMapping("/update")
    public String updateFile(HttpServletRequest request, Long fileId,MultipartFile file, Model model) throws IOException {
        //  更新就覆盖对应文件

        // 更新文件大小
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        String path = fileService.findPathById(fileId);
        String fileName = fileService.findNameById(fileId);
        Long fileSize = fileService.findSizeById(fileId);
        //Long spaceUsedSize = spaceService.findUsedSizeById(user.getUserId());

        // used_size = used_size + #{fileSize}
        //                           - fileSize + file.getSize()
        Boolean result = spaceService.updateUsedSize(user.getUserId(), file.getSize() - fileSize);
        if (result){
            File dstFile = new File(path +"\\" +fileName);
            file.transferTo(dstFile);
        }
        // todo 添加完整
        return "redirect:/user/size";
    }

    // 文件置顶
    @PostMapping("/pintop")
    public String pinTop(HttpServletRequest request, Long fileId, Model model){
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        if (user.getRole().equals("admin")){
            Boolean b = fileService.pinTop(fileId,2);
        } else {
            Boolean b = fileService.pinTop(fileId,1);
        }

        return "redirect:/user/size";
    }
    // 取消置顶
    @PostMapping("/calpintop")
    public String calPinTop(HttpServletRequest request, Long fileId, Model model){
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        fileService.calPinTop(fileId);
        return "redirect:/user/size";
    }

    // todo 图片轮播
    @GetMapping("/listImg")
    public String listImg(HttpServletRequest request, Model model){
        User user = (User) request.getSession().getAttribute("loginUser");
        if (user == null) {
            return "redirect:/user/login";
        }
        List<String> imgs = fileService.listImg(user.getUserId());
        model.addAttribute("imgPathList", imgs);
        return "user/space";
    }


}