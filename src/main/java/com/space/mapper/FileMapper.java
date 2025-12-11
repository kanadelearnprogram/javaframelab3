package com.space.mapper;

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
    

    /**
     * 查找所有文件
     * @return 文件列表
     */
    @Select("SELECT file_id, user_id, file_name, file_path, file_size, file_type, upload_time, status, download_count " +
            "FROM t_file")
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

}