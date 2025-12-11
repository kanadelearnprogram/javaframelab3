<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>页面未找到</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
</head>
<body>
<div class="container">
    <h2>404 - 页面未找到</h2>
    <p>抱歉，您访问的页面不存在。</p>
    <a href="<c:url value='/'/>">返回首页</a>
</div>
</body>
</html>