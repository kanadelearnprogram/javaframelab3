package com.space.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 空间实体类
 * 对应数据库表：t_space
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Space {
    // 空间ID（自增主键）
    private Long spaceId;
    // 所属用户ID（逻辑关联t_user）
    private Long userId;
    // 总容量（字节），默认5MB=5*1024*1024
    private Long totalSize;
    // 已用容量（实时更新）
    private Long usedSize;
    // 扩容申请容量（字节）
    private Long applySize;
    // 申请状态：未申请/待审核/通过/驳回
    private String applyStatus;
    // 扩容申请提交时间
    private Date applyTime;
    // 关联审核记录ID（逻辑关联t_audit_record）
    private Long auditId;
    // 创建时间
    private Date createTime;
    // 更新时间
    private Date updateTime;
}