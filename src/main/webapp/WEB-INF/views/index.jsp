<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>个人空间系统</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
</head>
<body>
    <div class="container">
        <h2>欢迎来到个人空间系统</h2>
        
        <c:if test="${not empty loginUser}">
            <p>欢迎您，${loginUser.nickname}!</p>
            <p>您的账号：${loginUser.account}</p>
            <p>用户角色：${loginUser.role}</p>
        </c:if>
        
        <c:if test="${empty loginUser}">
            <p>您尚未登录，请先<a href="<c:url value='/user/login'/>">登录</a></p>
        </c:if>
        
        <div class="links">
            <p><a href="<c:url value='/user/logout'/>">退出登录</a></p>
        </div>
    </div>
</body>
</html>