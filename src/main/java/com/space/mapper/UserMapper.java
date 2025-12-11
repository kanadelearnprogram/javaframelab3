package com.space.mapper;

import com.space.model.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

public interface UserMapper {

    @Insert("INSERT INTO t_user (account, password, nickname, register_time, status, role) " +
            "VALUES (#{account}, #{password}, #{nickname}, NOW(), #{status}, #{role})")
    boolean register(User user);

    @Select("SELECT * FROM `t_user` WHERE `account` = #{account} AND `password` = #{password}")
    User login(String account,String password);

    @Select("select * from t_user where user_id =#{userId}")
    User selectUserById(Long userId);
}