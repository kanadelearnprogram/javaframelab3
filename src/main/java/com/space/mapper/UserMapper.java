package com.space.mapper;

import com.space.model.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Insert("INSERT INTO t_user (account, password, nickname, register_date, status, role) "+
            "VALUES (#{account}, #{password}, #{nickname}, NOW(), #{status}, #{role})")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    int register(User user);

    @Select("SELECT * FROM t_user WHERE account = #{account} AND password = #{password}")
    User login(@Param("account") String account, @Param("password") String password);
    
    @Select("SELECT * FROM t_user WHERE user_id = #{userId}")
    User selectUserById(@Param("userId") Long userId);
}