<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>存储空间详情</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
    <style>
        .uploaded-files ul {
            list-style-type: none;
            padding: 0;
        }
        
        .uploaded-files li {
            padding: 10px;
            border-bottom: 1px solid #eee;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .file-actions {
            margin-left: 10px;
        }
        
        .file-actions .btn {
            padding: 5px 10px;
            font-size: 12px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>存储空间详情</h2>
        
        <c:if test="${not empty message}">
            <div class="alert alert-info">${message}</div>
        </c:if>
        
        <div class="user-info">
            <h3>用户ID: ${userId}</h3>
            <p>存储空间详情页面</p>
            <!-- 这里将来会显示实际的空间使用情况 -->
        </div>
        
        <!-- 文件上传表单 -->
        <div class="upload-section">
            <h3>文件上传</h3>
            <form action="<c:url value='/upload'/>" method="post" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="file">选择文件:</label>
                    <input type="file" id="file" name="file" required>
                </div>
                <button type="submit" class="btn primary">上传文件</button>
            </form>
        </div>
        
        <!-- 已上传文件列表 -->
        <div class="uploaded-files">
            <h3>已上传文件</h3>
            <c:choose>
                <c:when test="${not empty uploadedFiles && uploadedFiles.size() > 0}">
                    <ul>
                        <c:forEach var="file" items="${uploadedFiles}">
                            <li>
                                <span>${file}</span>
                                <span class="file-actions">
                                    <a href="<c:url value='/download/${file}'/>" class="btn secondary">下载</a>
                                </span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p>暂无上传文件</p>
                </c:otherwise>
            </c:choose>
        </div>
        
        <div class="links">
            <a href="<c:url value='/home'/>">返回首页</a>
        </div>
    </div>
</body>
</html>