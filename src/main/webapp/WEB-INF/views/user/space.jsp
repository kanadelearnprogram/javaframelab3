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
        
        /* 添加文件选择样式 */
        input[type="file"] {
            border: 1px solid #ddd;
            padding: 5px;
            border-radius: 3px;
        }
        
        /* 分页样式 */
        .pagination {
            margin-top: 20px;
            text-align: center;
        }
        
        .pagination .btn {
            margin: 0 5px;
            padding: 5px 10px;
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
<%--        todo 空间申请--%>
<%--        图显示已使用,未使用--%>
        
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
<%--         增加 冻结/解冻 按钮 删除 链接--%>
<%--        todo 分页 pagesize pagenum --%>
        <div class="uploaded-files">
            <h3>已上传文件</h3>
            <c:choose>
                <c:when test="${not empty uploadedFiles && uploadedFiles.size() > 0}">
                    <ul>
                        <c:forEach var="file" items="${uploadedFiles}">
                            <li>
                                <span>${file.fileName}</span>
                                <span class="file-actions">
                                    <a href="<c:url value='/download/${file.id}'/>" class="btn secondary">下载</a>
                                    <c:choose>
                                        <c:when test="${file.status == 1}">
                                            <form action="<c:url value='/unfreeze'/>" method="post" style="display: inline;">
                                                <input type="hidden" name="fileId" value="${file.id}">
                                                <button type="submit" class="btn warning">解冻</button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="<c:url value='/freeze'/>" method="post" style="display: inline;">
                                                <input type="hidden" name="fileId" value="${file.id}">
                                                <button type="submit" class="btn danger">冻结</button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                    
                                    <!-- 删除按钮 -->
                                    <form action="<c:url value='/delete'/>" method="post" style="display: inline;" onsubmit="return confirm('确定要删除这个文件吗？')">
                                        <input type="hidden" name="fileId" value="${file.id}">
                                        <button type="submit" class="btn danger">删除</button>
                                    </form>
                                    
                                    <!-- 更新按钮 -->
                                    <form action="<c:url value='/update'/>" method="post" enctype="multipart/form-data" style="display: inline;">
                                        <input type="hidden" name="fileId" value="${file.id}">
                                        <input type="file" name="file" style="width: 80px; display: inline-block;">
                                        <button type="submit" class="btn primary">更新</button>
                                    </form>
                                </span>
                            </li>
                        </c:forEach>
                    </ul>
                    
                    <!-- 分页控件  todo 添加选择页数分页-->
                    <div class="pagination">
                        <c:if test="${pageNum > 1}">
                            <a href="<c:url value='/user/size?pageNum=${pageNum - 1}&pageSize=${pageSize}'/>" class="btn">上一页</a>
                        </c:if>
                        
                        <span>第 ${pageNum} 页</span>
                        
                        <c:if test="${not empty uploadedFiles && uploadedFiles.size() == pageSize}">
                            <a href="<c:url value='/user/size?pageNum=${pageNum + 1}&pageSize=${pageSize}'/>" class="btn">下一页</a>
                        </c:if>
                    </div>
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
    
    <script>
        // 添加JavaScript代码来更新文件选择提示
        document.getElementById('file').addEventListener('change', function(e) {
            const fileName = e.target.files[0] ? e.target.files[0].name : '未选择文件';
            e.target.previousElementSibling.textContent = '选择文件: ' + fileName;
        });
    </script>
</body>
</html>