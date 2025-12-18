<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>云存空间 - 用户首页</title>
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
            <h2>用户控制台</h2>
            <c:if test="${not empty loginUser}">
                <div class="user-dashboard">
                    <h3>我的空间</h3>
                    <p>欢迎回来，您可以开始管理您的文件了。</p>
                    <div class="actions">
                        <a href="<c:url value='/user/size'/>" class="btn primary">查看存储空间</a>
                        <a href="<c:url value='/upload'/>" class="btn secondary">文件管理</a>
                    </div>
                </div>
            </c:if>
        </section>

        <!-- 显示所有文件 -->
<%--        todo 文件大小MB--%>
        <section class="files-section">
            <h3>所有文件</h3>
            <c:if test="${not empty allFiles}">
                <div style="margin-bottom: 10px;">
                    <a href="<c:url value='/home?sort=download'/>">按下载量排序</a> | 
                    <a href="<c:url value='/home'/>">默认排序</a>
                </div>
            </c:if>
            <c:choose>
                <c:when test="${not empty allFiles}">
                    <table class="files-table">
                        <thead>
                            <tr>
                                <th>所有者</th>
                                <th>文件名</th>
                                <th>文件大小</th>
                                <th>下载量</th>
                                <th>更新时间</th>
                                <th>操作</th>
                            </tr>
                        </thead>
<%--                        todo 添加置顶文件块--%>
                        <tbody>
                            <c:forEach var="file" items="${allFiles}">
                                <tr>
                                    <td>${ownerMap[file.userId]}</td>
                                    <td>${file.fileName}</td>
                                    <td>
                                        <fmt:formatNumber value="${file.fileSize/1024}" maxFractionDigits="0"/>KB
                                    </td>
                                    <td>${file.downloadCount}</td>
                                    <td>${fn:substring(file.uploadTime, 0, 10)}</td>
                                    <td><a href="<c:url value='/download/${file.id}'/>" class="btn secondary">下载</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>暂无文件</p>
                </c:otherwise>
            </c:choose>
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
        </section>
    </div>
</body>
</html>