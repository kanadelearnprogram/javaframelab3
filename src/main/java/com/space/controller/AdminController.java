package com.space.controller;

import com.space.mapper.FileMapper;
import com.space.mapper.SpaceMapper;
import com.space.model.entity.AuditRecord;
import com.space.model.entity.Files;
import com.space.model.entity.Space;
import com.space.model.entity.User;
import com.space.service.FileService;
import com.space.service.UserService;
import com.space.service.impl.FileServiceImpl;
import com.space.service.impl.UserServiceImpl;
import com.space.util.MyBatisUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
        
        // 获取待审核的空间扩容申请列表
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            SpaceMapper spaceMapper = sqlSession.getMapper(SpaceMapper.class);
            List<Space> pendingExpansions = spaceMapper.findPendingExpansions();
            model.addAttribute("pendingExpansions", pendingExpansions);
        } finally {
            sqlSession.close();
        }

        return "admin/dashboard";
    }

    // 审核文件操作
    @PostMapping("/review")
    public String reviewFile(@RequestParam("id") Long fileId,
                             @RequestParam("action") String action,
                             @RequestParam(value = "reason", required = false) String reason,
                             HttpServletRequest request,
                             Model model) {
        // 检查用户权限
        if (!isAdmin(request)) {
            return "redirect:/user/login";
        }

        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
            
            // 根据操作类型处理文件审核
            if ("approve".equals(action)) {
                // 插入审核记录
                AuditRecord auditRecord = new AuditRecord();
                auditRecord.setRelatedId(fileId);
                auditRecord.setRelatedType("file");
                auditRecord.setAuditUserId(loginUser.getUserId());
                auditRecord.setAuditResult("通过");
                auditRecord.setAuditReason("审核通过");
                auditRecord.setAuditTime(new Date());
                auditRecord.setCreateTime(new Date());
                fileMapper.insertAuditRecord(auditRecord);
                sqlSession.commit();
                
                // 更新文件状态
                fileService.approveFile(fileId, loginUser.getUserId());
                model.addAttribute("message", "文件审核通过成功");
            } else if ("reject".equals(action)) {
                String auditReason = (reason != null && !reason.isEmpty()) ? reason : "审核驳回";
                
                // 插入审核记录
                AuditRecord auditRecord = new AuditRecord();
                auditRecord.setRelatedId(fileId);
                auditRecord.setRelatedType("file");
                auditRecord.setAuditUserId(loginUser.getUserId());
                auditRecord.setAuditResult("驳回");
                auditRecord.setAuditReason(auditReason);
                auditRecord.setAuditTime(new Date());
                auditRecord.setCreateTime(new Date());
                fileMapper.insertAuditRecord(auditRecord);
                sqlSession.commit();
                
                // 更新文件状态
                fileService.rejectFile(fileId, loginUser.getUserId(), reason);
                model.addAttribute("message", "文件审核驳回成功");
            }
        } catch (Exception e) {
            sqlSession.rollback();
            model.addAttribute("error", "审核操作失败: " + e.getMessage());
        } finally {
            sqlSession.close();
        }

        return "redirect:/admin/dashboard";
    }
    
    // 审核空间扩容申请操作
    @PostMapping("/review-expansion")
    public String reviewExpansion(@RequestParam("userId") Long userId,
                                  @RequestParam("action") String action,
                                  @RequestParam(value = "reason", required = false) String reason,
                                  HttpServletRequest request,
                                  Model model) {
        // 检查用户权限
        if (!isAdmin(request)) {
            return "redirect:/user/login";
        }

        User loginUser = (User) request.getSession().getAttribute("loginUser");
        
        SqlSession sqlSession = MyBatisUtil.getSession();
        try {
            SpaceMapper spaceMapper = sqlSession.getMapper(SpaceMapper.class);
            
            // 根据操作类型处理空间扩容申请审核
            if ("approve".equals(action)) {
                // 插入审核记录
                AuditRecord auditRecord = new AuditRecord();
                auditRecord.setRelatedId(userId);
                auditRecord.setRelatedType("space");
                auditRecord.setAuditUserId(loginUser.getUserId());
                auditRecord.setAuditResult("通过");
                auditRecord.setAuditReason("审核通过");
                auditRecord.setAuditTime(new Date());
                auditRecord.setCreateTime(new Date());
                spaceMapper.insertAuditRecord(auditRecord);
                sqlSession.commit();
                
                // 获取刚刚插入的审核记录ID
                Long auditId = spaceMapper.getLastInsertId();
                
                // 更新空间信息
                spaceMapper.approveExpansion(userId, auditId);
                model.addAttribute("message", "空间扩容申请审核通过成功");
            } else if ("reject".equals(action)) {
                String auditReason = (reason != null && !reason.isEmpty()) ? reason : "审核驳回";
                
                // 插入审核记录
                AuditRecord auditRecord = new AuditRecord();
                auditRecord.setRelatedId(userId);
                auditRecord.setRelatedType("space");
                auditRecord.setAuditUserId(loginUser.getUserId());
                auditRecord.setAuditResult("驳回");
                auditRecord.setAuditReason(auditReason);
                auditRecord.setAuditTime(new Date());
                auditRecord.setCreateTime(new Date());
                spaceMapper.insertAuditRecord(auditRecord);
                sqlSession.commit();
                
                // 获取刚刚插入的审核记录ID
                Long auditId = spaceMapper.getLastInsertId();
                
                // 更新空间信息
                spaceMapper.rejectExpansion(userId, auditId);
                model.addAttribute("message", "空间扩容申请审核驳回成功");
            }
            
            sqlSession.commit();
        } catch (Exception e) {
            sqlSession.rollback();
            model.addAttribute("error", "审核操作失败: " + e.getMessage());
        } finally {
            sqlSession.close();
        }

        return "redirect:/admin/dashboard";
    }
}