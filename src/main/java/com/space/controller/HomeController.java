package com.space.controller;

import com.space.mapper.FileMapper;
import com.space.mapper.UserMapper;
import com.space.model.entity.Files;
import com.space.model.entity.User;
import com.space.util.MyBatisUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    // 网站首页路由（JSTL 渲染文件列表的核心请求）
    @GetMapping("/")
    public String index(@RequestParam(value = "sort", required = false) String sort,
                        HttpServletRequest request, Model model) {
        // 1. 获取登录用户（传给前端显示）
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        model.addAttribute("loginUser", loginUser);

        // 2. 获取所有文件列表（MyBatis 查询）
        SqlSession sqlSession = null;
        try {
            sqlSession = MyBatisUtil.getSession();
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            
            // 根据sort参数决定使用哪种排序方式
            List<Files> allFiles;
            if ("download".equals(sort)) {
                allFiles = fileMapper.findAllOrderByDownloadCountDesc(); // 按下载量排序
            } else {
                allFiles = fileMapper.findAll(); // 默认排序
            }

            // 3. 构建「文件所有者ID -> 昵称」的Map（解决前端ownerMap为空的问题）
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            Map<Long, String> ownerMap = new HashMap<>();
            for (Files file : allFiles) {
                User fileOwner = userMapper.selectUserById(file.getUserId());
                if (fileOwner != null) {
                    ownerMap.put(file.getUserId(), fileOwner.getNickname()); // 存入用户ID和昵称的映射
                } else {
                    ownerMap.put(file.getUserId(), "未知用户"); // 兜底
                }
            }

            // 4. 把数据传入Model（供JSTL渲染）
            model.addAttribute("allFiles", allFiles); // 文件列表
            model.addAttribute("ownerMap", ownerMap); // 所有者映射表

        } finally {
            if (sqlSession != null) {
                sqlSession.close(); // 确保SqlSession关闭
            }
        }

        return "index"; // 返回到index.jsp，此时JSTL会渲染Model中的数据
    }

    // 用户首页路由
    @GetMapping("/home")
    public String home(@RequestParam(value = "sort", required = false) String sort,
                       HttpServletRequest request, Model model) {
        // 1. 校验登录状态
        User loginUser = (User) request.getSession().getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/user/login"; // 未登录跳登录
        }
        model.addAttribute("loginUser", loginUser);

        // 2. 查询文件列表 + 构建所有者映射（核心补充）
        SqlSession sqlSession = null;
        try {
            // todo 修改成多表查询
            sqlSession = MyBatisUtil.getSession();
            // 2.1 查询当前登录用户的文件列表（也可查所有文件，按需调整）
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            
            // 根据sort参数决定使用哪种排序方式
            List<Files> allFiles;
            if ("download".equals(sort)) {
                allFiles = fileMapper.findAllOrderByDownloadCountDesc(); // 按下载量排序
            } else {
                allFiles = fileMapper.findAll(); // 默认排序
            }

            // 2.2 构建 ownerMap（文件所有者ID -> 昵称）
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            Map<Long, String> ownerMap = new HashMap<>();
            for (Files file : allFiles) {
                User fileOwner = userMapper.selectUserById(file.getUserId());
                ownerMap.put(file.getUserId(), fileOwner != null ? fileOwner.getNickname() : "未知用户");
            }

            // 2.3 把数据传入 Model（供 home.jsp 的 JSTL 渲染）
            model.addAttribute("allFiles", allFiles);
            model.addAttribute("ownerMap", ownerMap);

        } finally {
            if (sqlSession != null) sqlSession.close(); // 确保关闭连接
        }

        return "home"; // 对应 home.jsp，此时能拿到 allFiles/ownerMap
    }
}