<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>管理员审核面板</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
    <style>
        .admin-dashboard {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .pending-files-table, .pending-expansions-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        
        .pending-files-table th,
        .pending-files-table td,
        .pending-expansions-table th,
        .pending-expansions-table td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }
        
        .pending-files-table th,
        .pending-expansions-table th {
            background-color: #f2f2f2;
            font-weight: bold;
        }
        
        .pending-files-table tr:nth-child(even),
        .pending-expansions-table tr:nth-child(even) {
            background-color: #f9f9f9;
        }
        
        .pending-files-table tr:hover,
        .pending-expansions-table tr:hover {
            background-color: #f5f5f5;
        }
        
        .file-preview {
            width: 50px;
            height: 50px;
            display: flex;
            align-items: center;
            justify-content: center;
            background-color: #f0f0f0;
            border-radius: 4px;
            overflow: hidden;
        }
        
        .file-preview img {
            max-width: 100%;
            max-height: 100%;
            object-fit: cover;
        }
        
        .file-preview .file-icon {
            font-size: 24px;
        }
        
        .action-buttons {
            display: flex;
            gap: 10px;
        }
        
        .approve-btn {
            background-color: #4CAF50;
            color: white;
            border: none;
            padding: 8px 16px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            cursor: pointer;
            border-radius: 4px;
        }
        
        .reject-btn {
            background-color: #f44336;
            color: white;
            border: none;
            padding: 8px 16px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
            font-size: 14px;
            cursor: pointer;
            border-radius: 4px;
        }
        
        .reject-reason {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-family: Arial, sans-serif;
        }
        
        .notification {
            padding: 15px;
            margin-bottom: 20px;
            border-radius: 4px;
        }
        
        .notification.success {
            background-color: #dff0d8;
            color: #3c763d;
            border: 1px solid #d6e9c6;
        }
        
        .notification.error {
            background-color: #f2dede;
            color: #a94442;
            border: 1px solid #ebccd1;
        }
        
        .modal {
            display: none;
            position: fixed;
            z-index: 1;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            overflow: auto;
            background-color: rgb(0,0,0);
            background-color: rgba(0,0,0,0.4);
        }
        
        .modal-content {
            background-color: #fefefe;
            margin: 15% auto;
            padding: 20px;
            border: 1px solid #888;
            width: 50%;
            border-radius: 5px;
        }
        
        .close {
            color: #aaa;
            float: right;
            font-size: 28px;
            font-weight: bold;
        }
        
        .close:hover,
        .close:focus {
            color: black;
            text-decoration: none;
            cursor: pointer;
        }
        
        .section-title {
            margin-top: 30px;
            padding-bottom: 10px;
            border-bottom: 1px solid #eee;
        }
    </style>
</head>
<body>
    <div class="admin-dashboard">
        <h2>管理员审核面板</h2>
        
        <c:if test="${not empty message}">
            <div class="notification success">${message}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="notification error">${error}</div>
        </c:if>
        
        <div class="welcome">
            <p>欢迎，管理员 ${loginUser.nickname}!</p>
            <a href="<c:url value='/home'/>">返回首页</a>
        </div>
        
        <h3 class="section-title">待审核文件列表</h3>
        
        <c:choose>
            <c:when test="${not empty pendingFiles && pendingFiles.size() > 0}">
                <table class="pending-files-table">
                    <thead>
                        <tr>
                            <th>文件名</th>
                            <th>文件类型</th>
                            <th>上传用户ID</th>
                            <th>上传时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="file" items="${pendingFiles}">
                            <tr>
                                <td>
                                    <div style="display: flex; align-items: center; gap: 10px;">
                                        <div class="file-preview">
                                            <c:choose>
                                                <c:when test='${file.fileType == "图片"}'>
                                                    <span class="file-icon">🖼️</span>
                                                </c:when>
                                                <c:when test='${file.fileType == "文档"}'>
                                                    <span class="file-icon">📄</span>
                                                </c:when>
                                                <c:when test='${file.fileType == "音频"}'>
                                                    <span class="file-icon">🎵</span>
                                                </c:when>
                                                <c:when test='${file.fileType == "视频"}'>
                                                    <span class="file-icon">🎬</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="file-icon">📁</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                        <span>${file.fileName}</span>
                                    </div>
                                </td>
                                <td>${file.fileType}</td>
                                <td>${file.userId}</td>
                                <td>${file.uploadTime}</td>
                                <td>
                                    <div class="action-buttons">
                                        <form action="<c:url value='/admin/review'/>" method="post" style="display: inline;">
                                            <input type="hidden" name="fileId" value="${file.id}">
                                            <input type="hidden" name="action" value="approve">
                                            <button type="submit" class="approve-btn">通过</button>
                                        </form>
                                        
                                        <button type="button" class="reject-btn" onclick="openRejectModal('file', ${file.id})">驳回</button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>暂无待审核的文件。</p>
            </c:otherwise>
        </c:choose>
        
        <h3 class="section-title">待审核空间扩容申请</h3>
        
        <c:choose>
            <c:when test="${not empty pendingExpansions && pendingExpansions.size() > 0}">
                <table class="pending-expansions-table">
                    <thead>
                        <tr>
                            <th>申请人账号</th>
                            <th>用户ID</th>
                            <th>当前总空间(MB)</th>
                            <th>申请扩容空间(MB)</th>
                            <th>申请时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="expansion" items="${pendingExpansions}">
                            <tr>
                                <td>${expansion.userId != null ? expansion.userId : '未知用户'}</td>
                                <td>${expansion.userId}</td>
                                <td><fmt:formatNumber value="${expansion.totalSize / (1024*1024)}" maxFractionDigits="0"/></td>
                                <td><fmt:formatNumber value="${expansion.applySize / (1024*1024)}" maxFractionDigits="0"/></td>
                                <td>${expansion.applyTime}</td>
                                <td>
                                    <div class="action-buttons">
                                        <form action="<c:url value='/admin/review-expansion'/>" method="post" style="display: inline;">
                                            <input type="hidden" name="userId" value="${expansion.userId}">
                                            <input type="hidden" name="action" value="approve">
                                            <button type="submit" class="approve-btn">通过</button>
                                        </form>
                                        
                                        <button type="button" class="reject-btn" onclick="openRejectModal('expansion', ${expansion.userId})">驳回</button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:when>
            <c:otherwise>
                <p>暂无待审核的空间扩容申请。</p>
            </c:otherwise>
        </c:choose>
    </div>
    
    <!-- 驳回原因模态框 -->
    <div id="rejectModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeRejectModal()">&times;</span>
            <h3>请输入驳回原因</h3>
            <form id="rejectForm" method="post">
                <input type="hidden" name="action" value="reject">
                <input type="hidden" name="id" id="rejectId">
                <input type="hidden" name="type" id="rejectType">
                <textarea name="reason" class="reject-reason" rows="4" placeholder="请输入驳回原因（可选）"></textarea>
                <br><br>
                <button type="submit" class="reject-btn">确认驳回</button>
            </form>
        </div>
    </div>
    
    <script>
        // 打开驳回模态框
        function openRejectModal(type, id) {
            document.getElementById('rejectType').value = type;
            document.getElementById('rejectId').value = id;
            
            // 根据类型设置表单action
            const form = document.getElementById('rejectForm');
            if (type === 'file') {
                form.action = '<c:url value="/admin/review"/>';
            } else if (type === 'expansion') {
                form.action = '<c:url value="/admin/review-expansion"/>';
            }
            
            document.getElementById('rejectModal').style.display = 'block';
        }
        
        // 关闭驳回模态框
        function closeRejectModal() {
            document.getElementById('rejectModal').style.display = 'none';
        }
        
        // 点击模态框外部关闭
        window.onclick = function(event) {
            const modal = document.getElementById('rejectModal');
            if (event.target == modal) {
                modal.style.display = 'none';
            }
        }
    </script>
</body>
</html>