package com.space.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 审核记录实体类
 * 对应数据库表：t_audit_record
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class AuditRecord {
    // 审核ID（自增主键）
    private Long auditId;
    // 关联ID（file_id/）
    private Long relatedId;
    // 空间
    private Long relatedSpaceId;
    // 关联类型：file（文件审核）/space（扩容审核）
    private String relatedType;
    // 审核人ID（逻辑关联t_user）
    private Long auditUserId;
    // 审核时间
    private Date auditTime;
    // 审核结果：通过/驳回/冻结（仅文件）
    private String auditResult;
    // 驳回原因（可选）
    private String auditReason;
    // 审核备注（如扩容额度、文件违规点）
    private String auditRemark;
    // 创建时间
    private Date createTime;
}