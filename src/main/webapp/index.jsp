<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>个人空间系统</title>
</head>
<body>
    <!-- 页面将自动重定向到登录页面 -->
    <%
        response.sendRedirect(request.getContextPath() + "/user/login");
    %>
    <p>如果没有自动跳转，请点击 <a href="<%=request.getContextPath()%>/user/login">这里</a> 前往登录页面</p>
</body>
</html>