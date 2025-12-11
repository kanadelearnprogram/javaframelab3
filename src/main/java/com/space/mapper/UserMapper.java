package com.space.mapper;

import com.space.model.entity.User;
import org.apache.ibatis.annotations.*;

public interface UserMapper {

    @Insert("INSERT INTO t_user (account, password, nickname, register_date, status, role) "+
            "VALUES (#{account}, #{password}, #{nickname}, NOW(), #{status}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int register(User user);

    @Select("SELECT * FROM t_user WHERE account = #{account} AND password = #{password}")
    @Results({
            @Result(property = "userId", column = "user_id"), // 手动映射user_id→userId
    })
    User login(@Param("account") String account, @Param("password") String password);
    
    @Select("SELECT * FROM t_user WHERE user_id = #{userId}")
    User selectUserById(@Param("userId") Long userId);
}