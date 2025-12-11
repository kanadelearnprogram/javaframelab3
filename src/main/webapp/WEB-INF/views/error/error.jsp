<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>发生异常</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
</head>
<body>
<div class="container">
    <h2>发生异常</h2>
    <p>抱歉，系统发生了异常：<%=exception.getMessage()%></p>
    <a href="<c:url value='/'/>">返回首页</a>
</div>
</body>
</html>