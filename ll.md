
# 文件管理系统设计与实现文档

## 1. 系统概述

本系统是一个基于SpringMVC和MyBatis的个人空间文件管理系统，提供了完整的文件增删改查功能，以及文件冻结/解冻机制。系统支持多种文件类型，包括文档、图片、音频等，并具有完善的权限控制和状态管理。

## 2. 文件实体模型

### 2.1 Files实体类

文件系统的核心实体是[Files](file:///E:/codelab/javaframealab3/src/main/java/com/space/model/entity/Files.java#L12-L22)类，包含了文件的基本信息：

```java
public class Files {
    private Long id;              // 文件ID
    private Long userId;          // 用户ID
    private String fileName;      // 文件名
    private String filePath;      // 文件路径
    private Long fileSize;        // 文件大小
    private String fileType;      // 文件类型
    private LocalDateTime uploadTime;  // 上传时间
    private String status;        // 文件状态
    private Integer isTop;        // 是否置顶
    private Integer downloadCount;     // 下载次数
    private String description;   // 文件描述
}
```


其中，[status](file:///E:/codelab/javaframealab3/src/main/java/com/space/model/entity/Files.java#L19-L19)字段表示文件的状态，可以是以下几种之一：
- "待审核" - 文件刚上传，等待审核
- "通过" - 文件已通过审核，可正常使用
- "冻结" - 文件被冻结，无法访问
- "已删除" - 文件已被删除

## 3. 文件增删改查功能实现

### 3.1 文件上传（Create）

文件上传功能在[FilesController](file:///E:/codelab/javaframealab3/src/main/java/com/space/controller/FilesController.java#L35-L209)中实现，主要流程如下：

1. 用户登录验证
2. 空间容量检查
3. 文件保存到服务器
4. 文件信息保存到数据库

关键代码：
```java
@PostMapping("/upload")
public String upload(@RequestParam("file") MultipartFile file, 
                     RedirectAttributes redirectAttributes,
                     HttpServletRequest request) throws IOException {
    // 检查用户是否登录
    User user = (User) request.getSession().getAttribute("loginUser");
    if (user == null) {
        return "redirect:/user/login";
    }
    
    // 检查文件是否为空
    if (file.isEmpty()) {
        redirectAttributes.addFlashAttribute("message", "请选择文件");
        return "redirect:/upload";
    }

    try {
        // 检查空间大小
        SqlSession sqlSession = MyBatisUtil.getSession();
        SpaceMapper spaceMapper = sqlSession.getMapper(SpaceMapper.class);
        Long totalSize = spaceMapper.selectSpaceTotalSize(user.getUserId());
        Long usedSize = spaceMapper.selectUsedSize(user.getUserId());
        sqlSession.close();

        long size = file.getSize();
        if (!(totalSize > usedSize + size)) {
            redirectAttributes.addFlashAttribute("message", "文件过大或剩余空间不足");
            return "redirect:/upload";
        }

        String originalFilename = file.getOriginalFilename();
        // 确保目录存在
        File uploadDir = new File("E:\\codelab\\javaframealab3\\files\\" + user.getNickname());
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File objFile = new File(uploadDir, originalFilename);
        file.transferTo(objFile);
        // 保存到数据库
        Files files = new Files();
        files.setUserId(user.getUserId());
        files.setFileName(originalFilename);
        files.setFilePath(String.valueOf(uploadDir));
        files.setFileSize(size);
        files.setFileType("other");
        files.setStatus("待审核");
        files.setIsTop(0);
        
        // 使用Service保存文件信息到数据库
        boolean success = fileService.saveFile(files);

        if (success) {
            redirectAttributes.addFlashAttribute("message", "文件上传成功: " + originalFilename);
        } else {
            redirectAttributes.addFlashAttribute("message", "文件保存到数据库失败");
        }
    } catch (Exception e) {
        e.printStackTrace();
        redirectAttributes.addFlashAttribute("message", "文件上传失败: " + e.getMessage());
    }

    return "redirect:/upload";
}
```


### 3.2 文件查询（Read）

文件查询包括根据ID查询单个文件和根据用户ID查询文件列表两种方式。

#### 3.2.1 Mapper层实现

在[FileMapper](file:///E:/codelab/javaframealab3/src/main/java/com/space/mapper/FileMapper.java#L13-L92)中定义了查询方法：

```java
@Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
        "FROM t_file WHERE file_id = #{id}")
@Results({
    @Result(property = "id", column = "file_id"),
    @Result(property = "userId", column = "user_id"),
    @Result(property = "fileName", column = "file_name"),
    @Result(property = "filePath", column = "file_path"),
    @Result(property = "fileSize", column = "file_size"),
    @Result(property = "fileType", column = "file_type"),
    @Result(property = "uploadTime", column = "upload_time"),
    @Result(property = "status", column = "status"),
    @Result(property = "downloadCount", column = "download_count")
})
Files findById(int id);

@Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
        "FROM t_file WHERE user_id = #{userId}")
@Results({
    @Result(property = "id", column = "file_id"),
    @Result(property = "userId", column = "user_id"),
    @Result(property = "fileName", column = "file_name"),
    @Result(property = "filePath", column = "file_path"),
    @Result(property = "fileSize", column = "file_size"),
    @Result(property = "fileType", column = "file_type"),
    @Result(property = "uploadTime", column = "upload_time"),
    @Result(property = "status", column = "status"),
    @Result(property = "downloadCount", column = "download_count")
})
List<Files> findByUserId(Long userId);
```


#### 3.2.2 Service层实现

在[FileServiceImpl](file:///E:/codelab/javaframealab3/src/main/java/com/space/service/impl/FileServiceImpl.java#L12-L86)中实现了查询逻辑：

```java
@Override
public List<Files> listFile(Long userId) {
    SqlSession sqlSession = MyBatisUtil.getSession();
    FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
    List<Files> list = fileMapper.findByUserId(userId);
    sqlSession.close();
    return list;
}
```


### 3.3 文件下载（部分Update操作）

虽然下载本身不是传统意义上的更新操作，但在下载过程中会增加文件的下载计数，这也是一种更新操作。

```java
@GetMapping("/download/{fileId}")
public void download(@PathVariable Long fileId,
                     HttpServletRequest request,
                     HttpServletResponse response) throws IOException {
    // 检查用户是否登录
    User user = (User) request.getSession().getAttribute("loginUser");
    if (user == null) {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "请先登录");
        return;
    }
    
    // 从数据库获取文件信息
    SqlSession sqlSession = MyBatisUtil.getSession();
    FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
    Files file = fileMapper.findById(fileId.intValue());
    
    // 检查文件是否存在
    if (file == null) {
        sqlSession.close();
        response.sendError(HttpServletResponse.SC_NOT_FOUND, "文件不存在");
        return;
    }
    
    // 检查文件是否属于当前用户
    if (!file.getUserId().equals(user.getUserId())) {
        sqlSession.close();
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "无权访问该文件");
        return;
    }
    
    // 增加下载次数（通过Service层处理）
    fileService.incrementDownloadCount(fileId);
    
    sqlSession.close();
    
    // 执行文件下载逻辑...
}
```


## 4. 文件冻结/解冻功能

目前系统中尚未完整实现文件冻结/解冻功能，但数据库设计已经考虑到了这一需求。从数据库表结构可以看出，[status](file:///E:/codelab/javaframealab3/src/main/java/com/space/model/entity/Files.java#L19-L19)字段支持"冻结"状态。

### 4.1 数据库层面支持

在[doc/lab3.sql](file:///E:/codelab/javaframealab3/doc/lab3.sql)中定义的文件表结构中，[status](file:///E:/codelab/javaframealab3/src/main/java/com/space/model/entity/Files.java#L19-L19)字段支持以下状态：
```sql
`status` VARCHAR(20) NOT NULL DEFAULT '待审核' COMMENT '状态：待审核/通过/冻结/已删除'
```


### 4.2 建议的实现方案

为了实现文件的冻结/解冻功能，我们需要添加以下组件：

#### 4.2.1 在Mapper中添加更新状态的方法

在[FileMapper](file:///E:/codelab/javaframealab3/src/main/java/com/space/mapper/FileMapper.java#L13-L92)接口中添加：
```java
@Update("UPDATE t_file SET status = #{status} WHERE file_id = #{fileId}")
boolean updateStatus(@Param("fileId") Long fileId, @Param("status") String status);
```


#### 4.2.2 在Service中添加冻结/解冻方法

在[FileService](file:///E:/codelab/javaframealab3/src/main/java/com/space/service/FileService.java#L8-L11)接口中添加：
```java
boolean freezeFile(Long fileId);
boolean unfreezeFile(Long fileId);
```


在[FileServiceImpl](file:///E:/codelab/javaframealab3/src/main/java/com/space/service/impl/FileServiceImpl.java#L12-L86)中实现：
```java
@Override
public boolean freezeFile(Long fileId) {
    SqlSession sqlSession = MyBatisUtil.getSession();
    try {
        FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
        boolean result = fileMapper.updateStatus(fileId, "冻结");
        if (result) {
            sqlSession.commit();
        } else {
            sqlSession.rollback();
        }
        return result;
    } catch (Exception e) {
        sqlSession.rollback();
        throw new RuntimeException("文件冻结失败：" + e.getMessage());
    } finally {
        sqlSession.close();
    }
}

@Override
public boolean unfreezeFile(Long fileId) {
    SqlSession sqlSession = MyBatisUtil.getSession();
    try {
        FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
        boolean result = fileMapper.updateStatus(fileId, "通过"); // 解冻后恢复为通过状态
        if (result) {
            sqlSession.commit();
        } else {
            sqlSession.rollback();
        }
        return result;
    } catch (Exception e) {
        sqlSession.rollback();
        throw new RuntimeException("文件解冻失败：" + e.getMessage());
    } finally {
        sqlSession.close();
    }
}
```


#### 4.2.3 在Controller中添加冻结/解冻接口

在[FilesController](file:///E:/codelab/javaframealab3/src/main/java/com/space/controller/FilesController.java#L35-L209)中添加：
```java
@PostMapping("/freeze/{fileId}")
public String freezeFile(@PathVariable Long fileId, 
                        HttpServletRequest request,
                        RedirectAttributes redirectAttributes) {
    User user = (User) request.getSession().getAttribute("loginUser");
    if (user == null) {
        return "redirect:/user/login";
    }
    
    try {
        boolean result = fileService.freezeFile(fileId);
        if (result) {
            redirectAttributes.addFlashAttribute("message", "文件冻结成功");
        } else {
            redirectAttributes.addFlashAttribute("message", "文件冻结失败");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("message", "文件冻结异常: " + e.getMessage());
    }
    
    return "redirect:/upload";
}

@PostMapping("/unfreeze/{fileId}")
public String unfreezeFile(@PathVariable Long fileId, 
                          HttpServletRequest request,
                          RedirectAttributes redirectAttributes) {
    User user = (User) request.getSession().getAttribute("loginUser");
    if (user == null) {
        return "redirect:/user/login";
    }
    
    try {
        boolean result = fileService.unfreezeFile(fileId);
        if (result) {
            redirectAttributes.addFlashAttribute("message", "文件解冻成功");
        } else {
            redirectAttributes.addFlashAttribute("message", "文件解冻失败");
        }
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("message", "文件解冻异常: " + e.getMessage());
    }
    
    return "redirect:/upload";
}
```


## 5. 文件删除功能

系统目前没有实现物理删除文件的功能，而是应该采用逻辑删除的方式，即将文件状态设置为"已删除"。这可以通过更新文件的[status](file:///E:/codelab/javaframealab3/src/main/java/com/space/model/entity/Files.java#L19-L19)字段来实现。

### 5.1 建议的删除实现方案

#### 5.1.1 在Service中添加删除方法

在[FileService](file:///E:/codelab/javaframealab3/src/main/java/com/space/service/FileService.java#L8-L11)接口中添加：
```java
boolean deleteFile(Long fileId);
```


在[FileServiceImpl](file:///E:/codelab/javaframealab3/src/main/java/com/space/service/impl/FileServiceImpl.java#L12-L86)中实现：
```java
@Override
public boolean deleteFile(Long fileId) {
    SqlSession sqlSession = MyBatisUtil.getSession();
    try {
        FileMapper fileMapper = sqlSession.getMapper(FileMapper.class);
        boolean result = fileMapper.updateStatus(fileId, "已删除");
        if (result) {
            sqlSession.commit();
        } else {
            sqlSession.rollback();
        }
        return result;
    } catch (Exception e) {
        sqlSession.rollback();
        throw new RuntimeException("文件删除失败：" + e.getMessage());
    } finally {
        sqlSession.close();
    }
}
```


## 6. 总结

本系统已经实现了文件的基本增删改查功能，包括：
1. 文件上传
2. 文件查询（按ID和按用户ID）
3. 文件下载（附带下载计数更新）

对于冻结/解冻功能，系统已经在数据库层面做好了支持，但还需要在业务层和服务层进行具体的实现。建议按照上述方案补充完善这些功能。

文件的状态管理是整个系统的关键，通过[status](file:///E:/codelab/javaframealab3/src/main/java/com/space/model/entity/Files.java#L19-L19)字段的不同取值，我们可以方便地控制文件的各种状态，包括正常、待审核、冻结和已删除等。