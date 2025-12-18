package com.space.mapper;

import com.space.model.Paths;
import com.space.model.entity.Files;
import org.apache.ibatis.annotations.*;

import java.util.List;


public interface FileMapper {


    @Insert("INSERT INTO t_file(user_id, file_name, file_path, file_size, file_type, status, is_top) " +
            "VALUES(#{userId}, #{fileName}, #{filePath}, #{fileSize}, #{fileType}, #{status}, #{isTop})")
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
     boolean save(Files files) ;

    /**
     * 根据ID查找文件
     * @param id 文件ID
     * @return 文件对象
     */
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
    
    /**
     * 根据用户ID查找文件列表
     * @param userId 用户ID
     * @return 文件列表
     */
    @Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
            "FROM t_file WHERE user_id = #{userId} and review = 1")
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
    
    /**
     * 根据用户ID查找文件列表（分页）
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 文件列表
     */
    @Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
            "FROM t_file WHERE user_id = #{userId} and is_delete = 0 and review = 1 LIMIT #{offset}, #{limit}")
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
    List<Files> findByUserIdWithPagination(@Param("userId") Long userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查找所有文件
     * @return 文件列表
     */
    @Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
            "FROM t_file where is_delete = 0 and status =0 and review = 1")
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
    List<Files> findAll();
    
    /**
     * 查找所有文件并按下载量排序
     * @return 文件列表
     */
    @Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
            "FROM t_file where is_delete = 0 and status =0 ORDER BY download_count DESC and review = 1")
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
    List<Files> findAllOrderByDownloadCountDesc();
    
    /**
     * 增加文件下载次数
     * @param fileId 文件ID
     * @return 更新是否成功
     */
    @Update("UPDATE t_file SET download_count = download_count + 1 WHERE file_id = #{fileId}")
    boolean incrementDownloadCount(@Param("fileId") Long fileId);

    /**
     * 获取文件下载次数
     * @param fileId 文件ID
     * @return 下载次数
     */
    @Select("SELECT download_count FROM t_file WHERE file_id = #{fileId}")
    Integer getDownloadCount(@Param("fileId") Long fileId);

    @Update("update t_file set status = 1 where file_id = #{fileId}")
    boolean freezeFile(@Param("fileId") Long fileId);

    @Update("update t_file set status = 0 where file_id = #{fileId}")
    boolean unfreezeFile(@Param("fileId") Long fileId);

    @Update("update t_file set is_delete = 1 where file_id = #{fileId}")
    boolean deleteFile(@Param("fileId") Long fileId);

    @Select("select file_path from t_file where file_id = #{fileId}")
    String findPathById(@Param("fileId") Long fileId);

    @Select("select file_size from t_file where file_id = #{fileId}")
    Long findSizeById(@Param("fileId") Long fileId);

    @Select("select file_name from t_file where file_id = #{fileId}")
    String findNameById(Long fileId);

    @Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
            "FROM t_file where is_delete = 0 and status =0 order by download_count desc ")
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
    // 按下载量排序
    List<Files> findHotFiles();

    @Update("update t_file set is_top = #{isTop} where file_id = #{fileId} and review = 1")
    boolean pinTop(@Param("fileId") Long fileId,@Param("isTop")Integer isTop);

    @Update("update t_file set is_top = 0 where file_id = #{fileId}")
    boolean calPinTop(@Param("fileId") Long fileId);

    //
    @Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
            "FROM t_file WHERE user_id = #{userId} and review = 1 and is_top >0 ") //normal
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
    List<Files> findTopFiles(@Param("userId")Long userId);

    @Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
            "FROM t_file WHERE user_id = #{userId} and is_top = 2 and review = 1") //normal
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
    List<Files> findTopFilesByAdmin(Long userId);

    @Select("SELECT file_id, file_name, file_path " +
            "FROM t_file where user_id = #{userId} and is_delete = 0 and status = 0 and file_type = '图片' and review = 1 ")
    @Results({
            @Result(property = "id", column = "file_id"),
            @Result(property = "fileName", column = "file_name"),
            @Result(property = "filePath", column = "file_path"),
    })
    List<Paths> findAllImgPath(@Param("userId") Long userId);
    
    /**
     * 查找待审核的文件
     * @return 文件列表
     */
    @Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
            "FROM t_file WHERE review = 0 and is_delete = 0")
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
    List<Files> findPendingFiles();
    
    /**
     * 审核通过文件
     * @param fileId 文件ID
     * @return 更新是否成功
     */
    @Update("UPDATE t_file SET review = 1 WHERE file_id = #{fileId}")
    boolean approveFile(@Param("fileId") Long fileId);
    
    /**
     * 驳回文件
     * @param fileId 文件ID
     * @return 更新是否成功
     */
    @Update("UPDATE t_file SET review = 2 WHERE file_id = #{fileId}")
    boolean rejectFile(@Param("fileId") Long fileId);
    
    /**
     * 插入审核记录
     * @param relatedId 关联ID
     * @param relatedType 关联类型
     * @param auditUserId 审核人ID
     * @param auditResult 审核结果
     * @param auditReason 审核原因
     * @return 插入是否成功
     */
    @Insert("INSERT INTO t_audit_record(related_id, related_type, audit_user_id, audit_result, audit_reason) " +
            "VALUES(#{relatedId}, #{relatedType}, #{auditUserId}, #{auditResult}, #{auditReason})")
    boolean insertAuditRecord(@Param("relatedId") Long relatedId, 
                              @Param("relatedType") String relatedType, 
                              @Param("auditUserId") Long auditUserId, 
                              @Param("auditResult") String auditResult, 
                              @Param("auditReason") String auditReason);
}