<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户注册</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
</head>
<body>
    <div class="container">
        <h2>用户注册</h2>
        
        <c:if test="${not empty error}">
            <div class="error-message">
                ${error}
            </div>
        </c:if>
        
        <c:if test="${not empty message}">
            <div class="success-message">
                ${message}
            </div>
        </c:if>
        
        <form action="<c:url value='/user/register'/>" method="post">
            <div class="form-group">
                <label for="account">账号:</label>
                <input type="text" id="account" name="account" required>
            </div>
            
            <div class="form-group">
                <label for="password">密码:</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <div class="form-group">
                <label for="nickname">昵称:</label>
                <input type="text" id="nickname" name="nickname" required>
            </div>
            
            <div class="form-group">
                <button type="submit">注册</button>
            </div>
        </form>
        
        <div class="links">
            <p>已有账号? <a href="<c:url value='/user/login'/>">立即登录</a></p>
        </div>
    </div>
</body>
</html>