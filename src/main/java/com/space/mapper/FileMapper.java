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
    @Select("SELECT id, user_id, file_name, file_path, file_size, file_type, upload_time, status, is_public, download_count, description " +
            "FROM file WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "fileName", column = "file_name"),
        @Result(property = "filePath", column = "file_path"),
        @Result(property = "fileSize", column = "file_size"),
        @Result(property = "fileType", column = "file_type"),
        @Result(property = "uploadTime", column = "upload_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "isPublic", column = "is_public"),
        @Result(property = "downloadCount", column = "download_count"),
        @Result(property = "description", column = "description")
    })
    Files findById(int id);
    
    /**
     * 根据用户ID查找文件列表
     * @param userId 用户ID
     * @return 文件列表
     */
    @Select("SELECT id, user_id, file_name, file_path, file_size, file_type, upload_time, status, is_public, download_count, description " +
            "FROM file WHERE user_id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "fileName", column = "file_name"),
        @Result(property = "filePath", column = "file_path"),
        @Result(property = "fileSize", column = "file_size"),
        @Result(property = "fileType", column = "file_type"),
        @Result(property = "uploadTime", column = "upload_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "isPublic", column = "is_public"),
        @Result(property = "downloadCount", column = "download_count"),
        @Result(property = "description", column = "description")
    })
    List<Files> findByUserId(int userId);
    

    /**
     * 查找所有文件
     * @return 文件列表
     */
    @Select("SELECT id, user_id, file_name, file_path, file_size, file_type, upload_time, status, is_public, download_count, description " +
            "FROM file")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "fileName", column = "file_name"),
        @Result(property = "filePath", column = "file_path"),
        @Result(property = "fileSize", column = "file_size"),
        @Result(property = "fileType", column = "file_type"),
        @Result(property = "uploadTime", column = "upload_time"),
        @Result(property = "status", column = "status"),
        @Result(property = "isPublic", column = "is_public"),
        @Result(property = "downloadCount", column = "download_count"),
        @Result(property = "description", column = "description")
    })
    List<Files> findAll();
}