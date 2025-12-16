<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>存储空间详情</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/static/css/style.css'/>">
    <style>
        .space-info {
            background-color: #f8f9fa;
            border: 1px solid #dee2e6;
            border-radius: 5px;
            padding: 20px;
            margin-bottom: 20px;
        }
        
        .space-bar {
            width: 100%;
            height: 20px;
            background-color: #e9ecef;
            border-radius: 10px;
            margin: 10px 0;
            overflow: hidden;
        }
        
        .space-used {
            height: 100%;
            background-color: #007bff;
            border-radius: 10px;
        }
        
        .space-stats {
            display: flex;
            justify-content: space-between;
            margin-top: 5px;
            font-size: 14px;
            color: #6c757d;
        }
        
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
        
        .file-info {
            display: flex;
            align-items: center;
            flex-grow: 1;
        }
        
        .file-preview {
            width: 50px;
            height: 50px;
            margin-right: 15px;
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
        
        .file-details {
            flex-grow: 1;
        }
        
        .file-name {
            font-weight: bold;
            display: block;
        }
        
        .file-type {
            font-size: 12px;
            color: #666;
        }
        
        .file-actions {
            margin-left: 10px;
            display: flex;
            align-items: center;
            gap: 5px;
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
        
        .expand-space-form {
            background-color: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 5px;
            padding: 15px;
            margin: 20px 0;
        }
        
        /* 媒体播放器容器 */
        .media-player-container {
            margin: 10px 0;
            text-align: center;
            background: #000;
            border-radius: 5px;
            overflow: hidden;
        }
        
        .media-player-container audio,
        .media-player-container video {
            width: 100%;
            max-width: 100%;
            outline: none;
        }
        
        .media-player-container img {
            max-width: 100%;
            max-height: 400px;
            display: block;
            margin: 0 auto;
        }
        
        /* 预览切换按钮 */
        .toggle-preview {
            background: none;
            border: none;
            color: #007bff;
            cursor: pointer;
            text-decoration: underline;
            font-size: 12px;
            padding: 2px 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>存储空间详情</h2>
        
        <c:if test="${not empty message}">
            <div class="alert alert-info">${message}</div>
        </c:if>
        
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>
        
        <!-- 空间使用情况 -->
        <div class="space-info">
            <h3>空间使用情况</h3>
            <p>用户ID: ${userId}</p>
            
            <div class="space-bar">
                <div class="space-used" style="width: ${usedSizePercent}%"></div>
            </div>
            
            <div class="space-stats">
                <span>已使用: ${usedSize} 字节 (<fmt:formatNumber value="${usedSizePercent}" pattern="#.##"/>%)</span>
                <span>总空间: ${totalSize} 字节</span>
            </div>
            
            <c:if test="${usedSizePercent >= 80}">
                <div class="alert alert-warning">
                    您的空间使用率已超过80%，建议及时清理文件或扩容空间。
                </div>
            </c:if>
        </div>
        
        <!-- 空间扩容申请 -->
        <div class="expand-space-form">
            <h3>空间扩容</h3>
            <p>每次扩容增加100MB空间</p>
            <form action="<c:url value='/user/addsize'/>" method="post">
                <button type="submit" class="btn primary" onclick="return confirm('确定要申请扩容100MB空间吗？')">申请扩容</button>
            </form>
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
                                <div class="file-info">
                                    <div class="file-preview">
                                        <c:choose>
                                            <c:when test='${file.fileType == "图片"}'>
                                                <!-- 对于图片文件，显示缩略图 -->
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
                                    <div class="file-details">
                                        <span class="file-name">${file.fileName}</span>
                                        <span class="file-type">${file.fileType}</span>
                                    </div>
                                </div>
                                <span class="file-actions">
                                    <c:if test='${file.fileType == "图片" || file.fileType == "音频" || file.fileType == "视频"}'>
                                        <button type="button" class="toggle-preview" 
                                                onclick="togglePreview(${file.id}, '${file.fileType}', this)">
                                            预览
                                        </button>
                                    </c:if>
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
                                
                                <!-- 媒体播放器容器（初始隐藏） -->
                                <div id="media-container-${file.id}" class="media-player-container" style="display: none;"></div>
                            </li>
                        </c:forEach>
                    </ul>
                    
                    <!-- 分页控件 -->
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
        
        // 切换预览功能
        function togglePreview(fileId, fileType, button) {
            const container = document.getElementById("media-container-" + fileId);
            const isVisible = container.style.display !== "none";
            
            // 隐藏所有媒体容器
            const allContainers = document.querySelectorAll('[id^="media-container-"]');
            allContainers.forEach(c => {
                c.style.display = "none";
            });
            
            // 暂停所有媒体播放
            const allMedia = document.querySelectorAll('audio, video');
            allMedia.forEach(media => {
                media.pause();
            });
            
            if (isVisible) {
                // 如果当前是显示的，则隐藏
                container.style.display = "none";
                button.textContent = "预览";
            } else {
                // 如果当前是隐藏的，则显示并加载媒体
                container.innerHTML = "";
                
                if (fileType === "图片") {
                    const img = document.createElement("img");
                    img.src = "<c:url value='/preview/'/>" + fileId;
                    img.alt = "图片预览";
                    container.appendChild(img);
                } else if (fileType === "音频") {
                    const audio = document.createElement("audio");
                    audio.controls = true;
                    audio.autoplay = true;
                    
                    const source = document.createElement("source");
                    source.src = "<c:url value='/preview/'/>" + fileId;
                    
                    audio.appendChild(source);
                    audio.innerHTML += "您的浏览器不支持音频元素。";
                    container.appendChild(audio);
                } else if (fileType === "视频") {
                    const video = document.createElement("video");
                    video.controls = true;
                    video.autoplay = true;
                    
                    const source = document.createElement("source");
                    source.src = "<c:url value='/preview/'/>" + fileId;
                    
                    video.appendChild(source);
                    video.innerHTML += "您的浏览器不支持视频元素。";
                    container.appendChild(video);
                }
                
                container.style.display = "block";
                button.textContent = "隐藏";
            }
        }
    </script>
</body>
</html>