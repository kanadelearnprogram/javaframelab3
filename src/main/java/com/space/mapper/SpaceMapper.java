package com.space.mapper;

import com.space.model.entity.AuditRecord;
import com.space.model.entity.Space;
import com.space.model.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface SpaceMapper {


    @Insert("INSERT INTO `t_space` (`user_id`, `total_size`, `used_size`, `apply_status`, `create_time`, `update_time`)" +
            "VALUES (#{userId},#{totalSize},#{usedSize}, 'Normal', NOW(), NOW())")
    boolean addUserSpace(Space space);

    @Select("select total_size from t_space where user_id = #{userId}")
    Long selectSpaceTotalSize(@Param("userId") Long userId);

    @Select("select used_size from t_space where user_id = #{userId}")
    Long selectUsedSize(Long userId);
    
    // 更新已使用空间大小
    @Update("UPDATE t_space SET used_size = used_size + #{fileSize} WHERE user_id = #{userId}")
    boolean updateUsedSize(@Param("userId") Long userId, @Param("fileSize") Long fileSize);

    // 更新总空间大小
    @Update("UPDATE t_space SET total_size = #{newTotalSize} WHERE user_id = #{userId}")
    boolean updateTotalSize(@Param("userId") Long userId, @Param("newTotalSize") Long newTotalSize);
    
    // 提交空间扩容申请
    @Update("UPDATE t_space SET apply_size = #{applySize}, apply_status = '待审核', apply_time = NOW() WHERE user_id = #{userId}")
    boolean submitExpansionApplication(@Param("userId") Long userId, @Param("applySize") Long applySize);
    
    // 通过空间扩容申请
    @Update("UPDATE t_space SET total_size = total_size + apply_size, apply_size = 0, apply_status = '通过', audit_id = #{auditId} WHERE user_id = #{userId}")
    boolean approveExpansion(@Param("userId") Long userId, @Param("auditId") Long auditId);
    
    // 拒绝空间扩容申请
    @Update("UPDATE t_space SET apply_size = 0, apply_status = '驳回', audit_id = #{auditId} WHERE user_id = #{userId}")
    boolean rejectExpansion(@Param("userId") Long userId, @Param("auditId") Long auditId);
    
    // 根据用户ID查找空间信息
    @Select("SELECT space_id, user_id, total_size, used_size, apply_size, apply_status, apply_time, audit_id, create_time, update_time FROM t_space WHERE user_id = #{userId}")
    @Results({
        @Result(property = "spaceId", column = "space_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "totalSize", column = "total_size"),
        @Result(property = "usedSize", column = "used_size"),
        @Result(property = "applySize", column = "apply_size"),
        @Result(property = "applyStatus", column = "apply_status"),
        @Result(property = "applyTime", column = "apply_time"),
        @Result(property = "auditId", column = "audit_id"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    Space findByUserId(@Param("userId") Long userId);
    
    // 查找所有有待审核扩容申请的空间
    @Select("SELECT s.space_id, s.user_id, s.total_size, s.used_size, s.apply_size, s.apply_status, s.apply_time, s.audit_id, s.create_time, s.update_time, u.account " +
            "FROM t_space s JOIN t_user u ON s.user_id = u.user_id WHERE s.apply_status = '待审核'")
    @Results({
        @Result(property = "spaceId", column = "space_id"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "totalSize", column = "total_size"),
        @Result(property = "usedSize", column = "used_size"),
        @Result(property = "applySize", column = "apply_size"),
        @Result(property = "applyStatus", column = "apply_status"),
        @Result(property = "applyTime", column = "apply_time"),
        @Result(property = "auditId", column = "audit_id"),
        @Result(property = "createTime", column = "create_time"),
        @Result(property = "updateTime", column = "update_time")
    })
    List<Space> findPendingExpansions();
    
    @Insert("INSERT INTO t_audit_record(related_id, related_type, audit_user_id, audit_result, audit_reason, audit_time, create_time) " +
            "VALUES(#{relatedId}, #{relatedType}, #{auditUserId}, #{auditResult}, #{auditReason}, #{auditTime}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "auditId", keyColumn = "audit_id")
    boolean insertAuditRecord(AuditRecord auditRecord);
                              
    @Select("SELECT LAST_INSERT_ID()")
    Long getLastInsertId();
}