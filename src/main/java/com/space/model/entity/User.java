package com.space.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户实体类
 * 对应数据库表：t_user
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class User {
    // 用户ID（自增主键）
    private Long userId;
    // 注册账号（唯一）
    private String account;
    // 加密密码（建议BCrypt加密）
    private String password;
    // 用户昵称
    private String nickname;
    // 注册日期
    private Date registerDate;
    // 状态：1-正常 0-冻结
    private Integer status;
    // 角色：管理员/普通用户/游客/系统用户
    private String role;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
}