# 个人空间系统API文档

## 1. 文档说明

本文档基于系统需求文档和数据库结构，详细描述了个人空间系统的API接口规范，包括接口定义、请求/响应格式、参数说明和示例。

## 2. 系统概述

个人空间系统采用SpringMVC+MyBatis架构，支持四大角色：普通用户、管理员、游客和审核员，提供用户注册、空间操作、文件管理、关注功能和审核机制等核心业务功能。

## 3. 数据模型

### 3.1 用户表（t_user）

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| user_id | BIGINT UNSIGNED | 用户ID（自增主键） |
| account | VARCHAR(50) | 注册账号（唯一） |
| password | VARCHAR(100) | 加密密码（BCrypt加密） |
| nickname | VARCHAR(50) | 用户昵称 |
| register_date | DATE | 注册日期 |
| status | TINYINT | 状态：1-正常 0-冻结 |
| role | VARCHAR(20) | 角色：管理员/普通用户/游客/系统用户 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 3.2 用户空间表（t_space）

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| space_id | BIGINT UNSIGNED | 空间ID（自增主键） |
| user_id | BIGINT UNSIGNED | 所属用户ID（逻辑关联t_user） |
| total_size | BIGINT | 总容量（字节），默认5MB |
| used_size | BIGINT | 已用容量（实时更新） |
| apply_size | BIGINT | 扩容申请容量（字节） |
| apply_status | VARCHAR(20) | 申请状态：未申请/待审核/通过/驳回 |
| apply_time | DATETIME | 扩容申请提交时间 |
| audit_id | BIGINT UNSIGNED | 关联审核记录ID（逻辑关联t_audit_record） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 3.3 文件表（t_file）

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| file_id | BIGINT UNSIGNED | 文件ID（自增主键） |
| user_id | BIGINT UNSIGNED | 所属用户ID（逻辑关联t_user） |
| file_type | VARCHAR(20) | 文件类型：文档/图片/相册/音乐 |
| file_name | VARCHAR(255) | 文件名 |
| file_path | VARCHAR(512) | 文件相对路径 |
| file_size | BIGINT | 文件大小（字节） |
| upload_time | DATETIME | 上传时间 |
| status | VARCHAR(20) | 状态：待审核/通过/冻结/已删除 |
| download_count | INT | 下载量 |
| is_top | TINYINT | 是否置顶：1-是 0-否 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### 3.4 用户关注表（t_follow）

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| follow_id | BIGINT UNSIGNED | 关注ID（自增主键） |
| user_id | BIGINT UNSIGNED | 被关注者ID（逻辑关联t_user） |
| follower_id | BIGINT UNSIGNED | 关注者ID（逻辑关联t_user） |
| follow_time | DATETIME | 关注时间 |
| status | TINYINT | 状态：1-有效 0-取消 |

### 3.5 审核记录表（t_audit_record）

| 字段名 | 数据类型 | 描述 |
| :--- | :--- | :--- |
| audit_id | BIGINT UNSIGNED | 审核ID（自增主键） |
| related_id | BIGINT UNSIGNED | 关联ID（file_id/space_id） |
| related_type | VARCHAR(20) | 关联类型：file（文件审核）/space（扩容审核） |
| audit_user_id | BIGINT UNSIGNED | 审核人ID（逻辑关联t_user） |
| audit_time | DATETIME | 审核时间 |
| audit_result | VARCHAR(20) | 审核结果：通过/驳回/冻结（仅文件） |
| audit_reason | VARCHAR(1024) | 驳回原因（可选） |
| audit_remark | VARCHAR(1024) | 审核备注（如扩容额度、文件违规点） |
| create_time | DATETIME | 创建时间 |

## 4. API接口

### 4.1 用户管理API

#### 4.1.1 用户注册

```
POST /api/users/register
```

请求参数：
```json
{
  "account": "string",
  "password": "string",
  "nickname": "string",
  "registerDate": "2025-01-01"
}
```

响应：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "userId": 1,
    "account": "string",
    "nickname": "string",
    "registerDate": "2025-01-01",
    "role": "普通用户",
    "status": 1
  }
}
```

#### 4.1.2 用户登录

```
POST /api/users/login
```

请求参数：
```json
{
  "account": "string",
  "password": "string"
}
```

响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "userId": 1,
    "account": "string",
    "nickname": "string",
    "role": "普通用户",
    "token": "string"
  }
}
```

#### 4.1.3 获取用户信息

```
GET /api/users/{userId}
```

请求参数：
- userId: 用户ID (路径参数)

响应：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "userId": 1,
    "account": "string",
    "nickname": "string",
    "registerDate": "2025-01-01",
    "status": 1,
    "role": "普通用户",
    "createTime": "2025-01-01 12:00:00",
    "updateTime": "2025-01-01 12:00:00"
  }
}
```

#### 4.1.4 更新用户信息

```
PUT /api/users/{userId}
```

请求参数：
```json
{
  "nickname": "新昵称"
}
```

响应：
```json
{
  "code": 200,
  "message": "更新成功"
}
```

#### 4.1.5 修改密码

```
PUT /api/users/{userId}/password
```

请求参数：
```json
{
  "oldPassword": "旧密码",
  "newPassword": "新密码"
}
```

响应：
```json
{
  "code": 200,
  "message": "密码修改成功"
}
```

#### 4.1.6 冻结/解冻用户 (管理员权限)

```
PUT /api/users/{userId}/status
```

请求参数：
```json
{
  "status": 0 // 0-冻结 1-解冻
}
```

响应：
```json
{
  "code": 200,
  "message": "操作成功"
}
```

### 4.2 空间管理API

#### 4.2.1 获取用户空间信息

```
GET /api/spaces/user/{userId}
```

请求参数：
- userId: 用户ID (路径参数)

响应：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "spaceId": 1,
    "userId": 1,
    "totalSize": 5242880, // 5MB
    "usedSize": 102400,
    "applySize": 0,
    "applyStatus": "未申请",
    "applyTime": null,
    "auditId": null,
    "createTime": "2025-01-01 12:00:00",
    "updateTime": "2025-01-01 12:00:00"
  }
}
```

#### 4.2.2 申请空间扩容

```
POST /api/spaces/apply
```

请求参数：
```json
{
  "userId": 1,
  "applySize": 10485760 // 申请增加10MB
}
```

响应：
```json
{
  "code": 200,
  "message": "申请提交成功",
  "data": {
    "spaceId": 1,
    "userId": 1,
    "applySize": 10485760,
    "applyStatus": "待审核",
    "applyTime": "2025-01-01 12:00:00"
  }
}
```

#### 4.2.3 更新空间扩容申请状态 (管理员权限)

```
PUT /api/spaces/audit/{auditId}
```

请求参数：
```json
{
  "auditResult": "通过", // 通过/驳回
  "auditRemark": "批准扩容10MB",
  "auditReason": "如果驳回，需要填写驳回原因"
}
```

响应：
```json
{
  "code": 200,
  "message": "审核操作成功"
}
```

### 4.3 文件管理API

#### 4.3.1 上传文件

```
POST /api/files/upload
```

请求参数：
- 表单数据: file (文件), userId (用户ID), fileType (文件类型：文档/图片/相册/音乐)

响应：
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "fileId": 1,
    "userId": 1,
    "fileName": "风景图.jpg",
    "filePath": "/static/images/scenery.jpg",
    "fileSize": 102400,
    "fileType": "图片",
    "status": "待审核",
    "uploadTime": "2025-01-01 12:00:00",
    "downloadCount": 0,
    "isTop": 0
  }
}
```

#### 4.3.2 获取用户文件列表

```
GET /api/files/user/{userId}
```

请求参数：
- userId: 用户ID (路径参数)
- page: 页码 (查询参数, 默认1)
- pageSize: 每页数量 (查询参数, 默认10)
- fileType: 文件类型 (查询参数, 可选)
- status: 文件状态 (查询参数, 可选)

响应：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "items": [
      {
        "fileId": 1,
        "fileName": "风景图.jpg",
        "fileType": "图片",
        "fileSize": 102400,
        "filePath": "/static/images/scenery.jpg",
        "status": "待审核",
        "uploadTime": "2025-01-01 12:00:00",
        "downloadCount": 0,
        "isTop": 0
      }
    ]
  }
}
```

#### 4.3.3 下载文件

```
GET /api/files/{fileId}/download
```

请求参数：
- fileId: 文件ID (路径参数)

响应：
- 文件数据流

#### 4.3.4 文件置顶/取消置顶

```
PUT /api/files/{fileId}/top
```

请求参数：
```json
{
  "isTop": 1 // 1-置顶 0-取消置顶
}
```

响应：
```json
{
  "code": 200,
  "message": "操作成功"
}
```

#### 4.3.5 更新文件状态 (管理员权限)

```
PUT /api/files/{fileId}/status
```

请求参数：
```json
{
  "status": "通过", // 通过/冻结/已删除
  "auditRemark": "审核通过"
}
```

响应：
```json
{
  "code": 200,
  "message": "状态更新成功"
}

#### 4.3.6 删除文件

```
DELETE /api/files/{fileId}
```

请求参数：
- fileId: 文件ID (路径参数)

响应：
```json
{
  "code": 200,
  "message": "删除成功"
}
```

### 4.4 关注功能API

#### 4.4.1 关注用户

```
POST /api/follows
```

请求参数：
```json
{
  "userId": 2, // 被关注者ID
  "followerId": 3 // 关注者ID
}
```

响应：
```json
{
  "code": 200,
  "message": "关注成功",
  "data": {
    "followId": 1,
    "userId": 2,
    "followerId": 3,
    "followTime": "2025-01-01 12:00:00",
    "status": 1
  }
}
```

#### 4.4.2 取消关注

```
DELETE /api/follows/{followerId}/{userId}
```

请求参数：
- followerId: 关注者ID (路径参数)
- userId: 被关注者ID (路径参数)

响应：
```json
{
  "code": 200,
  "message": "取消关注成功"
}
```

#### 4.4.3 获取用户粉丝列表

```
GET /api/users/{userId}/followers
```

请求参数：
- userId: 用户ID (路径参数)
- page: 页码 (查询参数, 默认1)
- pageSize: 每页数量 (查询参数, 默认10)

响应：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 50,
    "page": 1,
    "pageSize": 10,
    "items": [
      {
        "followerId": 3,
        "account": "visitor001",
        "nickname": "游客001",
        "followTime": "2025-01-01 12:00:00"
      }
    ]
  }
}

#### 4.4.4 获取用户关注列表

```
GET /api/users/{userId}/following
```

请求参数：
- userId: 用户ID (路径参数)
- page: 页码 (查询参数, 默认1)
- pageSize: 每页数量 (查询参数, 默认10)

响应：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 50,
    "page": 1,
    "pageSize": 10,
    "items": [
      {
        "userId": 2,
        "account": "user001",
        "nickname": "普通用户001",
        "followTime": "2025-01-01 12:00:00"
      }
    ]
  }
}

### 4.5 审核管理API

#### 4.5.1 获取待审核文件列表 (管理员权限)

```
GET /api/audit/files
```

请求参数：
- page: 页码 (查询参数, 默认1)
- pageSize: 每页数量 (查询参数, 默认10)

响应：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 50,
    "page": 1,
    "pageSize": 10,
    "items": [
      {
        "fileId": 1,
        "userId": 2,
        "account": "user001",
        "fileName": "风景图.jpg",
        "fileType": "图片",
        "fileSize": 102400,
        "filePath": "/static/images/scenery.jpg",
        "status": "待审核",
        "uploadTime": "2025-01-01 12:00:00"
      }
    ]
  }
}

#### 4.5.2 处理文件审核 (管理员权限)

```
POST /api/audit/file/{fileId}
```

请求参数：
```json
{
  "auditResult": "通过", // 通过/驳回/冻结
  "auditRemark": "审核通过",
  "auditReason": "如果驳回，需要填写驳回原因"
}
```

响应：
```json
{
  "code": 200,
  "message": "审核处理成功",
  "data": {
    "auditId": 1,
    "relatedId": 1,
    "relatedType": "file",
    "auditUserId": 1,
    "auditTime": "2025-01-01 12:30:00",
    "auditResult": "通过",
    "auditRemark": "审核通过"
  }
}

#### 4.5.3 获取待审核空间扩容列表 (管理员权限)

```
GET /api/audit/spaces
```

请求参数：
- page: 页码 (查询参数, 默认1)
- pageSize: 每页数量 (查询参数, 默认10)

响应：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 30,
    "page": 1,
    "pageSize": 10,
    "items": [
      {
        "spaceId": 1,
        "userId": 2,
        "account": "user001",
        "totalSize": 5242880,
        "usedSize": 102400,
        "applySize": 10485760,
        "applyStatus": "待审核",
        "applyTime": "2025-01-01 12:00:00"
      }
    ]
  }
}

#### 4.5.4 获取审核记录历史

```
GET /api/audit/history
```

请求参数：
- relatedType: 关联类型 (查询参数, 可选, file/space)
- auditResult: 审核结果 (查询参数, 可选)
- page: 页码 (查询参数, 默认1)
- pageSize: 每页数量 (查询参数, 默认10)

响应：
```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 100,
    "page": 1,
    "pageSize": 10,
    "items": [
      {
        "auditId": 1,
        "relatedId": 1,
        "relatedType": "file",
        "auditUserId": 1,
        "auditTime": "2025-01-01 12:30:00",
        "auditResult": "通过",
        "auditRemark": "审核通过",
        "createTime": "2025-01-01 12:30:00"
      }
    ]
  }
}
```

## 5. 统一响应格式

所有API接口采用统一的响应格式：

```json
{
  "code": 200,
  "message": "string",
  "data": {}
}
```

- code: 状态码，200表示成功，其他为错误码
- message: 提示信息
- data: 响应数据，根据接口不同返回不同格式的数据

## 6. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 400 | 请求参数错误 |
| 401 | 未授权或登录失效 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 7. 安全说明

- 所有非公开API需要在请求头中添加Authorization: Bearer {token}
- 密码存储采用加密处理
- 文件上传有大小和类型限制
- 敏感操作需要二次验证

## 8. 其他说明

- 接口频率限制：普通用户每分钟60次，VIP用户每分钟120次
- 大文件上传建议使用分块上传
- 批量操作有数量限制，单次不超过100条

## 9. JSP与SpringMVC绑定代码示例

### 9.1 用户管理相关功能

#### 9.1.1 用户注册功能

##### SpringMVC控制器（UserController.java）

```java
package com.space.controller;

import com.space.model.User;
import com.space.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // 前往注册页面
    @GetMapping("/register")
    public String toRegister() {
        return "user/register";
    }
    
    // 用户注册处理 - 支持日期类型绑定
    @PostMapping("/register")
    public String register(@RequestParam("account") String account,
                          @RequestParam("password") String password,
                          @RequestParam("nickname") String nickname,
                          @RequestParam("birthDate") 
                          @DateTimeFormat(pattern = "yyyy-MM-dd") Date birthDate,
                          Model model) {
        
        try {
            User user = new User();
            user.setAccount(account);
            user.setPassword(password); // 实际应用中应进行加密
            user.setNickname(nickname);
            user.setBirthDate(birthDate);
            user.setRegisterTime(new Date());
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
    
    // 路径传参示例 - 查看用户信息
    @GetMapping("/info/{userId}")
    public String userInfo(@PathVariable("userId") Long userId, Model model) {
        User user = userService.getUserById(userId);
        model.addAttribute("user", user);
        return "user/user-info";
    }
}
```

##### JSP页面（register.jsp）

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户注册 - 个人空间系统</title>
    <script type="text/javascript">
        // 删除确认示例
        function confirmDelete(userId) {
            if (confirm("确定要删除该用户吗？")) {
                window.location.href = "delete/" + userId;
            }
        }
    </script>
</head>
<body>
    <h2>用户注册</h2>
    
    <c:if test="${not empty error}">
        <div style="color: red;">${error}</div>
    </c:if>
    
    <form action="<c:url value='/user/register' />" method="post">
        <table>
            <tr>
                <td>账号：</td>
                <td><input type="text" name="account" required /></td>
            </tr>
            <tr>
                <td>密码：</td>
                <td><input type="password" name="password" required /></td>
            </tr>
            <tr>
                <td>昵称：</td>
                <td><input type="text" name="nickname" required /></td>
            </tr>
            <tr>
                <td>出生日期：</td>
                <td><input type="date" name="birthDate" required /></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="submit" value="注册" />
                    <input type="reset" value="重置" />
                </td>
            </tr>
        </table>
    </form>
    
    <p>已有账号？<a href="<c:url value='/user/login' />">去登录</a></p>
</body>
</html>
```

##### Service层（UserService.java）

```java
package com.space.service;

import com.space.model.User;

public interface UserService {
    boolean registerUser(User user);
    User getUserById(Long userId);
    User login(String account, String password);
    boolean updateUserInfo(User user);
    boolean deleteUser(Long userId);
}
```

##### Service实现层（UserServiceImpl.java）

```java
package com.space.service.impl;

import com.space.mapper.UserMapper;
import com.space.mapper.SpaceMapper;
import com.space.model.User;
import com.space.model.Space;
import com.space.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private SpaceMapper spaceMapper;
    
    // 使用事务确保用户注册和空间初始化的原子性
    @Transactional
    @Override
    public boolean registerUser(User user) {
        try {
            // 插入用户信息
            int result = userMapper.insertUser(user);
            
            if (result > 0) {
                // 获取自增主键
                Long userId = user.getUserId();
                
                // 初始化用户空间（默认5MB）
                Space space = new Space();
                space.setUserId(userId);
                space.setTotalSize(5 * 1024 * 1024L); // 5MB
                space.setUsedSize(0L);
                space.setStatus("NORMAL");
                space.setCreateTime(new java.util.Date());
                
                spaceMapper.insertSpace(space);
                return true;
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException("用户注册失败：" + e.getMessage());
        }
    }
    
    @Override
    public User getUserById(Long userId) {
        return userMapper.selectUserById(userId);
    }
    
    @Override
    public User login(String account, String password) {
        // 实际应用中应进行密码加密比较
        return userMapper.selectUserByAccountAndPassword(account, password);
    }
    
    @Override
    public boolean updateUserInfo(User user) {
        return userMapper.updateUser(user) > 0;
    }
    
    @Transactional
    @Override
    public boolean deleteUser(Long userId) {
        try {
            // 先删除用户空间
            spaceMapper.deleteSpaceByUserId(userId);
            // 再删除用户
            return userMapper.deleteUser(userId) > 0;
        } catch (Exception e) {
            throw new RuntimeException("删除用户失败：" + e.getMessage());
        }
    }
}
```

### 9.2 文件管理相关功能

#### 9.2.1 文件上传功能

##### SpringMVC控制器（FileController.java）

```java
package com.space.controller;

import com.space.model.File;
import com.space.model.User;
import com.space.service.FileService;
import com.space.service.SpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/file")
public class FileController {
    
    @Autowired
    private FileService fileService;
    
    @Autowired
    private SpaceService spaceService;
    
    // 前往文件上传页面
    @GetMapping("/upload")
    public String toUploadPage(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        // 获取用户空间使用情况
        long usedSize = spaceService.getSpaceUsedSize(currentUser.getUserId());
        long totalSize = spaceService.getSpaceTotalSize(currentUser.getUserId());
        
        model.addAttribute("usedSize", usedSize);
        model.addAttribute("totalSize", totalSize);
        model.addAttribute("userId", currentUser.getUserId());
        
        return "space/upload";
    }
    
    // 文件上传处理
    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                            @RequestParam("fileType") String fileType,
                            @RequestParam("description") String description,
                            HttpSession session,
                            Model model) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        try {
            // 检查文件大小是否超过剩余空间
            long fileSize = file.getSize();
            long remainingSpace = spaceService.getSpaceRemainingSize(currentUser.getUserId());
            
            if (fileSize > remainingSpace) {
                model.addAttribute("error", "文件大小超过剩余空间！");
                return toUploadPage(model, session);
            }
            
            // 创建文件实体
            File newFile = new File();
            newFile.setUserId(currentUser.getUserId());
            newFile.setFileName(file.getOriginalFilename());
            newFile.setFileSize(fileSize);
            newFile.setFileType(fileType);
            newFile.setDescription(description);
            newFile.setUploadTime(new Date());
            newFile.setStatus("PENDING"); // 待审核
            newFile.setDownloadCount(0);
            
            // 保存文件并更新空间使用情况
            boolean success = fileService.saveFile(newFile, file.getInputStream());
            
            if (success) {
                model.addAttribute("message", "文件上传成功，请等待审核！");
            } else {
                model.addAttribute("error", "文件上传失败，请重试");
            }
            
            return toUploadPage(model, session);
            
        } catch (IOException e) {
            model.addAttribute("error", "文件上传异常：" + e.getMessage());
            return toUploadPage(model, session);
        }
    }
    
    // 分页查询用户文件列表
    @GetMapping("/list")
    public String fileList(@RequestParam(defaultValue = "1") int pageNum,
                          @RequestParam(defaultValue = "10") int pageSize,
                          @RequestParam(required = false) String keyword,
                          HttpSession session,
                          Model model) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/user/login";
        }
        
        // 查询文件列表（带分页）
        List<File> fileList = fileService.getUserFiles(currentUser.getUserId(), pageNum, pageSize, keyword);
        // 查询总记录数
        int totalCount = fileService.getUserFileCount(currentUser.getUserId(), keyword);
        // 计算总页数
        int totalPages = (totalCount + pageSize - 1) / pageSize;
        
        model.addAttribute("fileList", fileList);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("keyword", keyword);
        
        return "space/file-manage";
    }
}
```

##### JSP页面（upload.jsp）

```jsp
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>文件上传 - 个人空间</title>
    <style>
        .progress-bar {
            width: 300px;
            height: 20px;
            background-color: #f0f0f0;
            border-radius: 10px;
            overflow: hidden;
        }
        .progress-fill {
            height: 100%;
            background-color: #4CAF50;
        }
    </style>
</head>
<body>
    <h2>文件上传</h2>
    
    <c:if test="${not empty error}">
        <div style="color: red;">${error}</div>
    </c:if>
    
    <c:if test="${not empty message}">
        <div style="color: green;">${message}</div>
    </c:if>
    
    <!-- 空间使用情况 -->
    <div>
        <p>空间使用情况：</p>
        <div class="progress-bar">
            <div class="progress-fill" style="width: ${(usedSize/totalSize)*100}%"></div>
        </div>
        <p>已用: ${usedSize/1024/1024}MB / ${totalSize/1024/1024}MB</p>
    </div>
    
    <!-- 文件上传表单 -->
    <form action="<c:url value='/file/upload' />" method="post" enctype="multipart/form-data">
        <table>
            <tr>
                <td>选择文件：</td>
                <td><input type="file" name="file" required /></td>
            </tr>
            <tr>
                <td>文件类型：</td>
                <td>
                    <select name="fileType" required>
                        <option value="document">文档</option>
                        <option value="image">图片</option>
                        <option value="audio">音频</option>
                        <option value="video">视频</option>
                        <option value="other">其他</option>
                    </select>
                </td>
            </tr>
            <tr>
                <td>文件描述：</td>
                <td><textarea name="description" rows="3" cols="40"></textarea></td>
            </tr>
            <tr>
                <td colspan="2">
                    <input type="submit" value="上传" />
                </td>
            </tr>
        </table>
    </form>
    
    <p><a href="<c:url value='/file/list' />">查看我的文件</a></p>
    <p><a href="<c:url value='/space/index' />">返回空间首页</a></p>
</body>
</html>
```

## 10. 数据库SQL语句示例

### 10.1 用户管理相关SQL

#### 10.1.1 用户表操作
```sql
-- 创建用户表
CREATE TABLE `t_user` (
  `user_id` BIGINT UNSIGNED AUTO_INCREMENT COMMENT '用户ID',
  `account` VARCHAR(50) NOT NULL COMMENT '账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) NOT NULL COMMENT '昵称',
  `birth_date` DATE DEFAULT NULL COMMENT '出生日期',
  `register_time` DATETIME NOT NULL COMMENT '注册时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1:正常,0:冻结)',
  `role` VARCHAR(20) NOT NULL DEFAULT '普通用户' COMMENT '角色：管理员/普通用户/游客/系统用户',
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `idx_account` (`account`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入用户示例数据
INSERT INTO `t_user` (`account`, `password`, `nickname`, `birth_date`, `register_time`, `status`, `role`)
VALUES 
('admin', 'admin123', '管理员', '1990-01-01', NOW(), 1, '管理员'),
('user1', 'user123', '测试用户', '1995-05-15', NOW(), 1, '普通用户');

-- 查询用户信息
SELECT * FROM `t_user` WHERE `user_id` = ?;
SELECT * FROM `t_user` WHERE `account` = ? AND `password` = ?;

-- 更新用户信息
UPDATE `t_user` SET `nickname` = ?, `birth_date` = ? WHERE `user_id` = ?;

-- 修改密码
UPDATE `t_user` SET `password` = ? WHERE `user_id` = ?;

-- 冻结/解冻用户
UPDATE `t_user` SET `status` = ? WHERE `user_id` = ?;

-- 删除用户
DELETE FROM `t_user` WHERE `user_id` = ?;
```

### 10.2 空间管理相关SQL

#### 10.2.1 空间表操作
```sql
-- 创建空间表
CREATE TABLE `t_space` (
  `space_id` BIGINT UNSIGNED AUTO_INCREMENT COMMENT '空间ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '用户ID',
  `total_size` BIGINT NOT NULL DEFAULT 5242880 COMMENT '总空间大小(字节)',
  `used_size` BIGINT NOT NULL DEFAULT 0 COMMENT '已用空间大小(字节)',
  `apply_size` BIGINT DEFAULT 0 COMMENT '扩容申请容量（字节）',
  `apply_status` VARCHAR(20) DEFAULT '未申请' COMMENT '申请状态：未申请/待审核/通过/驳回',
  `apply_time` DATETIME DEFAULT NULL COMMENT '扩容申请提交时间',
  `audit_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联审核记录ID',
  `status` VARCHAR(20) NOT NULL DEFAULT 'NORMAL' COMMENT '状态(NORMAL:正常,FULL:已满)',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`space_id`),
  UNIQUE KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户空间表';

-- 初始化用户空间
INSERT INTO `t_space` (`user_id`, `total_size`, `used_size`, `status`, `create_time`, `update_time`)
VALUES (?, 5242880, 0, 'NORMAL', NOW(), NOW());

-- 更新空间使用情况
UPDATE `t_space` SET `used_size` = `used_size` + ?, `update_time` = NOW() 
WHERE `user_id` = ?;

-- 查询用户空间信息
SELECT * FROM `t_space` WHERE `user_id` = ?;

-- 申请空间扩容
UPDATE `t_space` SET 
    `apply_size` = ?, 
    `apply_status` = '待审核', 
    `apply_time` = NOW(), 
    `update_time` = NOW() 
WHERE `user_id` = ?;

-- 更新扩容审核状态
UPDATE `t_space` SET 
    `apply_status` = ?, 
    `total_size` = CASE WHEN ? = '通过' THEN `total_size` + `apply_size` ELSE `total_size` END,
    `audit_id` = ?, 
    `update_time` = NOW() 
WHERE `user_id` = ?;
```

### 10.3 文件管理相关SQL

#### 10.3.1 文件表操作
```sql
-- 创建文件表
CREATE TABLE `t_file` (
  `file_id` BIGINT UNSIGNED AUTO_INCREMENT COMMENT '文件ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID',
  `file_name` VARCHAR(255) NOT NULL COMMENT '文件名称',
  `file_size` BIGINT NOT NULL COMMENT '文件大小(字节)',
  `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
  `file_type` VARCHAR(50) NOT NULL COMMENT '文件类型：文档/图片/相册/音乐',
  `description` TEXT COMMENT '文件描述',
  `upload_time` DATETIME NOT NULL COMMENT '上传时间',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态(PENDING:待审核,APPROVED:已通过,REJECTED:已拒绝,FROZEN:已冻结)',
  `download_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '下载次数',
  `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶(0:否,1:是)',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  `update_time` DATETIME NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`file_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_upload_time` (`upload_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- 插入文件记录
INSERT INTO `t_file` (`user_id`, `file_name`, `file_size`, `file_path`, `file_type`, `description`, `upload_time`, `status`, `download_count`, `is_top`, `create_time`, `update_time`)
VALUES (?, ?, ?, ?, ?, ?, NOW(), 'PENDING', 0, 0, NOW(), NOW());

-- 分页查询用户文件列表（支持模糊查询）
SELECT * FROM `t_file` 
WHERE `user_id` = ? 
AND (`file_name` LIKE CONCAT('%', ?, '%') OR `description` LIKE CONCAT('%', ?, '%')) 
ORDER BY `upload_time` DESC 
LIMIT ?, ?;

-- 查询总记录数
SELECT COUNT(*) FROM `t_file` 
WHERE `user_id` = ? 
AND (`file_name` LIKE CONCAT('%', ?, '%') OR `description` LIKE CONCAT('%', ?, '%'));

-- 更新文件状态
UPDATE `t_file` SET `status` = ?, `update_time` = NOW() WHERE `file_id` = ?;

-- 更新下载次数
UPDATE `t_file` SET `download_count` = `download_count` + 1 WHERE `file_id` = ?;

-- 设置文件置顶/取消置顶
UPDATE `t_file` SET `is_top` = ?, `update_time` = NOW() WHERE `file_id` = ?;

-- 删除文件
UPDATE `t_file` SET `status` = '已删除', `update_time` = NOW() WHERE `file_id` = ?;
```

### 10.4 关注功能相关SQL

#### 10.4.1 关注表操作
```sql
-- 创建关注表
CREATE TABLE `t_follow` (
  `follow_id` BIGINT UNSIGNED AUTO_INCREMENT COMMENT '关注记录ID',
  `user_id` BIGINT UNSIGNED NOT NULL COMMENT '被关注者ID',
  `follower_id` BIGINT UNSIGNED NOT NULL COMMENT '关注者ID',
  `follow_time` DATETIME NOT NULL COMMENT '关注时间',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-有效 0-取消',
  PRIMARY KEY (`follow_id`),
  UNIQUE KEY `idx_user_follower` (`user_id`, `follower_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_follower_id` (`follower_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='关注表';

-- 插入关注记录
INSERT INTO `t_follow` (`user_id`, `follower_id`, `follow_time`, `status`)
VALUES (?, ?, NOW(), 1);

-- 取消关注
UPDATE `t_follow` SET `status` = 0 WHERE `user_id` = ? AND `follower_id` = ?;

-- 查询用户关注列表（分页）
SELECT f.user_id, u.nickname, u.account, f.follow_time 
FROM `t_follow` f
JOIN `t_user` u ON f.user_id = u.user_id
WHERE f.follower_id = ? AND f.status = 1
ORDER BY f.follow_time DESC
LIMIT ?, ?;

-- 查询用户粉丝列表（分页）
SELECT f.follower_id, u.nickname, u.account, f.follow_time 
FROM `t_follow` f
JOIN `t_user` u ON f.follower_id = u.user_id
WHERE f.user_id = ? AND f.status = 1
ORDER BY f.follow_time DESC
LIMIT ?, ?;

-- 查询关注记录总数
SELECT COUNT(*) FROM `t_follow` WHERE `follower_id` = ? AND `status` = 1;
```

### 10.5 审核管理相关SQL

#### 10.5.1 审核记录表操作
```sql
-- 创建审核记录表
CREATE TABLE `t_audit_record` (
  `audit_id` BIGINT UNSIGNED AUTO_INCREMENT COMMENT '审核ID',
  `related_id` BIGINT UNSIGNED NOT NULL COMMENT '关联ID（file_id/space_id）',
  `related_type` VARCHAR(20) NOT NULL COMMENT '关联类型：file（文件审核）/space（扩容审核）',
  `audit_user_id` BIGINT UNSIGNED NOT NULL COMMENT '审核人ID',
  `audit_time` DATETIME NOT NULL COMMENT '审核时间',
  `audit_result` VARCHAR(20) NOT NULL COMMENT '审核结果：通过/驳回/冻结（仅文件）',
  `audit_reason` VARCHAR(1024) COMMENT '驳回原因（可选）',
  `audit_remark` VARCHAR(1024) COMMENT '审核备注（如扩容额度、文件违规点）',
  `create_time` DATETIME NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`audit_id`),
  KEY `idx_related_id_type` (`related_id`, `related_type`),
  KEY `idx_audit_time` (`audit_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

-- 插入审核记录
INSERT INTO `t_audit_record` (
    `related_id`, 
    `related_type`, 
    `audit_user_id`, 
    `audit_time`, 
    `audit_result`, 
    `audit_reason`, 
    `audit_remark`, 
    `create_time`
) VALUES (?, ?, ?, NOW(), ?, ?, ?, NOW());

-- 查询待审核文件列表（分页）
SELECT f.*, u.nickname, u.account FROM `t_file` f
JOIN `t_user` u ON f.user_id = u.user_id
WHERE f.status = 'PENDING'
ORDER BY f.upload_time DESC
LIMIT ?, ?;

-- 查询待审核空间扩容列表（分页）
SELECT s.*, u.nickname, u.account FROM `t_space` s
JOIN `t_user` u ON s.user_id = u.user_id
WHERE s.apply_status = '待审核'
ORDER BY s.apply_time DESC
LIMIT ?, ?;

-- 查询审核历史记录（分页）
SELECT a.*, u.nickname AS auditor_name 
FROM `t_audit_record` a
JOIN `t_user` u ON a.audit_user_id = u.user_id
WHERE (? IS NULL OR a.related_type = ?)
  AND (? IS NULL OR a.audit_result = ?)
ORDER BY a.audit_time DESC
LIMIT ?, ?;
```