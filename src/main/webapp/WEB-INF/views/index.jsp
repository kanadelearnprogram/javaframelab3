<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>云存空间 - 首页</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
</head>
<body>
<div class="container">
    <header>
        <h1>云存空间</h1>
        <nav>
            <a href="<c:url value='/'/>">首页</a>
            <c:if test="${empty loginUser}">
                <a href="<c:url value='/user/login'/>">登录</a>
                <a href="<c:url value='/user/register'/>">注册</a>
            </c:if>
            <c:if test="${not empty loginUser}">
                <span>欢迎，${loginUser.nickname}!</span>
                <a href="<c:url value='/user/logout'/>">退出</a>
            </c:if>
        </nav>
    </header>

    <section class="hero">
        <h2>安全可靠的云端存储服务</h2>
        <p>为您提供安全、便捷的文件存储和分享服务</p>
        <c:if test="${empty loginUser}">
            <div class="cta">
                <a href="<c:url value='/user/register'/>" class="btn primary">立即注册</a>
                <a href="<c:url value='/user/login'/>" class="btn secondary">用户登录</a>
            </div>
        </c:if>
    </section>

    <!-- JSTL 渲染所有文件列表（核心修复） -->
    <section class="files-section">
        <h3>所有文件</h3>
        <!-- 有文件时渲染表格 -->
        <c:if test="${not empty allFiles}">
            <table class="files-table">
                <thead>
                <tr>
                    <th>所有者</th>
                    <th>文件名</th>
                    <th>下载量</th>
                    <th>更新时间</th> <!-- 统一字段名：若实体类是uploadTime则改回 -->
                </tr>
                </thead>
                <tbody>
                <!-- JSTL 遍历文件列表 -->
                <c:forEach var="file" items="${allFiles}">
                    <tr>
                        <!-- 显示文件所有者昵称（从ownerMap中取） -->
                        <td>${ownerMap[file.userId]}</td>
                        <!-- 显示原始文件名（不再拼接昵称） -->
                        <td>${file.fileName}</td>
                        <td>${file.downloadCount}</td>
                        <!-- 注意：字段名需和Files实体类一致！若实体类是uploadTime则改为${file.uploadTime} -->
                        <td>${file.updateTime}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>
        <!-- 无文件时显示 -->
        <c:if test="${empty allFiles}">
            <p>暂无文件</p>
        </c:if>
    </section>

    <section class="features">
        <div class="feature">
            <h3>安全可靠</h3>
            <p>采用先进的加密技术保护您的数据安全</p>
        </div>
        <div class="feature">
            <h3>随时访问</h3>
            <p>支持多平台访问，随时随地管理您的文件</p>
        </div>
        <div class="feature">
            <h3>简单易用</h3>
            <p>直观的界面设计，轻松上手</p>
        </div>
</div>
</div>
</body>
</html>