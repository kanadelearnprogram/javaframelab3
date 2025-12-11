<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户登录</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
</head>
<body>
    <div class="form-container">
        <h2>用户登录</h2>
        
        <c:if test="${not empty error}">
            <div class="error-message">
                ${error}
            </div>
        </c:if>
        
        <form action="<c:url value='/user/login'/>" method="post">
            <div class="form-group">
                <label for="account">账号:</label>
                <input type="text" id="account" name="account" required>
            </div>
            
            <div class="form-group">
                <label for="password">密码:</label>
                <input type="password" id="password" name="password" required>
            </div>
            
            <div class="form-group">
                <button type="submit">登录</button>
            </div>
        </form>
        
        <div class="links">
            <p>还没有账号? <a href="<c:url value='/user/register'/>">立即注册</a></p>
            <p><a href="<c:url value='/'/>">返回首页</a></p>
        </div>
    </div>
</body>
</html>