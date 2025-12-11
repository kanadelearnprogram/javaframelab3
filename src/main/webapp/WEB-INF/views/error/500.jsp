<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>服务器内部错误</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
</head>
<body>
<div class="container">
    <h2>500 - 服务器内部错误</h2>
    <p>抱歉，服务器发生了内部错误。</p>
    <a href="<c:url value='/'/>">返回首页</a>
</div>
</body>
</html>