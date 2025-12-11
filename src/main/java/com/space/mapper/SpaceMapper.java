package com.space.mapper;

import com.space.model.entity.Space;
import com.space.model.entity.User;
import org.apache.ibatis.annotations.*;

public interface SpaceMapper {


    @Insert("INSERT INTO `t_space` (`user_id`, `total_size`, `used_size`, `apply_status`, `create_time`, `update_time`)" +
            "VALUES (#{userId},#{totalSize},#{usedSize}, 'Normal', NOW(), NOW())")
    boolean addUserSpace(Space space);

    @Select("select total_size from t_space where user_id = #{userId}")
    Long selectSpaceTotalSize(@Param("userId") Long userId);

    @Select("select used_size from t_space where user_id = #{userId}")
    Long selectUsedSize(Long userId);
    
    // 更新space 大小
    @Update("UPDATE t_space SET used_size = used_size + #{fileSize} WHERE user_id = #{userId}")
    boolean updateUsedSize(@Param("userId") Long userId, @Param("fileSize") Long fileSize);
}