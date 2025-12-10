-- 个人空间系统完整SQL脚本（MySQL 5.7+/8.0+）
-- 调整说明：1. 移除所有外键约束 2. 删除文件分类表 3. 文件表新增file_type字段存储文件类型
DROP DATABASE IF EXISTS space_system;
CREATE DATABASE IF NOT EXISTS space_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE space_system;

-- ----------------------------
-- 1. 用户表（t_user）：移除外键相关约束，仅保留核心字段
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
                          `user_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID（自增主键）',
                          `account` VARCHAR(50) NOT NULL COMMENT '注册账号（唯一）',
                          `password` VARCHAR(100) NOT NULL COMMENT '加密密码（建议BCrypt加密）',
                          `nickname` VARCHAR(50) NOT NULL COMMENT '用户昵称',
                          `register_date` DATE NOT NULL COMMENT '注册日期（支持yyyy-MM-dd/yyyy/MM/dd）',
                          `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-正常 0-冻结',
                          `role` VARCHAR(20) NOT NULL DEFAULT '普通用户' COMMENT '角色：管理员/普通用户/游客/系统用户',
                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
                          PRIMARY KEY (`user_id`),
                          UNIQUE KEY `uk_account` (`account`) COMMENT '账号唯一约束',
                          KEY `idx_role` (`role`) COMMENT '角色索引（按角色筛选用户）'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表（含角色）';

-- ----------------------------
-- 2. 审核记录表（t_audit_record）：移除外键约束，仅保留逻辑关联
-- ----------------------------
DROP TABLE IF EXISTS `t_audit_record`;
CREATE TABLE `t_audit_record` (
                                  `audit_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '审核ID（自增主键）',
                                  `related_id` BIGINT UNSIGNED NOT NULL COMMENT '关联ID（file_id/space_id）',
                                  `related_type` VARCHAR(20) NOT NULL COMMENT '关联类型：file（文件审核）/space（扩容审核）',
                                  `audit_user_id` BIGINT UNSIGNED NOT NULL COMMENT '审核人ID（逻辑关联t_user）',
                                  `audit_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '审核时间',
                                  `audit_result` VARCHAR(20) NOT NULL COMMENT '审核结果：通过/驳回/冻结（仅文件）',
                                  `audit_reason` VARCHAR(1024) DEFAULT NULL COMMENT '驳回原因（可选）',
                                  `audit_remark` VARCHAR(1024) DEFAULT NULL COMMENT '审核备注（如扩容额度、文件违规点）',
                                  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`audit_id`),
                                  KEY `idx_related_id_type` (`related_id`,`related_type`) COMMENT '关联ID+类型索引',
                                  KEY `idx_audit_user_id` (`audit_user_id`) COMMENT '审核人索引',
                                  KEY `idx_audit_time` (`audit_time`) COMMENT '审核时间索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表（文件+空间扩容）';

-- ----------------------------
-- 3. 用户空间表（t_space）：移除外键约束，仅保留逻辑关联
-- ----------------------------
DROP TABLE IF EXISTS `t_space`;
CREATE TABLE `t_space` (
                           `space_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '空间ID（自增主键）',
                           `user_id` BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID（逻辑关联t_user）',
                           `total_size` BIGINT NOT NULL DEFAULT 5242880 COMMENT '总容量（字节），默认5MB=5*1024*1024',
                           `used_size` BIGINT NOT NULL DEFAULT 0 COMMENT '已用容量（实时更新）',
                           `apply_size` BIGINT DEFAULT 0 COMMENT '扩容申请容量（字节）',
                           `apply_status` VARCHAR(20) DEFAULT '未申请' COMMENT '申请状态：未申请/待审核/通过/驳回',
                           `apply_time` DATETIME DEFAULT NULL COMMENT '扩容申请提交时间',
                           `audit_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '关联审核记录ID（逻辑关联t_audit_record）',
                           `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           PRIMARY KEY (`space_id`),
                           UNIQUE KEY `uk_user_id` (`user_id`) COMMENT '用户空间唯一',
                           KEY `idx_apply_status` (`apply_status`) COMMENT '申请状态索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户空间表（扩容申请+容量管理）';

-- ----------------------------
-- 4. 文件表（t_file）：删除category_id，新增file_type字段存储文件类型，移除外键
-- ----------------------------
DROP TABLE IF EXISTS `t_file`;
CREATE TABLE `t_file` (
                          `file_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '文件ID（自增主键）',
                          `user_id` BIGINT UNSIGNED NOT NULL COMMENT '所属用户ID（逻辑关联t_user）',
                          `file_type` VARCHAR(20) NOT NULL COMMENT '文件类型：文档/图片/相册/音乐',
                          `file_name` VARCHAR(255) NOT NULL COMMENT '文件名（支持模糊查询）',
                          `file_path` VARCHAR(512) NOT NULL COMMENT '文件相对路径（图片预览用）',
                          `file_size` BIGINT NOT NULL COMMENT '文件大小（字节）',
                          `upload_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
                          `status` VARCHAR(20) NOT NULL DEFAULT '待审核' COMMENT '状态：待审核/通过/冻结/已删除',
                          `download_count` INT NOT NULL DEFAULT 0 COMMENT '下载量',
                          `is_top` TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶：1-是 0-否',
                          `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (`file_id`),
                          KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
                          KEY `idx_file_name` (`file_name`) COMMENT '文件名索引',
                          KEY `idx_file_type` (`file_type`) COMMENT '文件类型索引',
                          KEY `idx_download_count` (`download_count`) COMMENT '下载量索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表（内置文件类型）';

-- ----------------------------
-- 5. 用户关注表（t_follow）：移除外键约束，仅保留逻辑关联
-- ----------------------------
DROP TABLE IF EXISTS `t_follow`;
CREATE TABLE `t_follow` (
                            `follow_id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '关注ID（自增主键）',
                            `user_id` BIGINT UNSIGNED NOT NULL COMMENT '被关注者ID（逻辑关联t_user）',
                            `follower_id` BIGINT UNSIGNED NOT NULL COMMENT '关注者ID（逻辑关联t_user）',
                            `follow_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
                            `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1-有效 0-取消',
                            PRIMARY KEY (`follow_id`),
                            UNIQUE KEY `uk_user_follower` (`user_id`,`follower_id`) COMMENT '避免重复关注',
                            KEY `idx_follower_id` (`follower_id`) COMMENT '关注者索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户关注表';

-- ----------------------------
-- 初始化基础数据
-- ----------------------------
-- 1. 管理员用户（密码：123456，BCrypt加密后；实际生产需替换为真实加密值）
INSERT INTO `t_user` (`account`, `password`, `nickname`, `register_date`, `status`, `role`)
VALUES ('admin', '$2a$10$e8V9k8jZ7b6s5d4f3g2h1a9s8d7f6g5h4j3k2l1m0n9b8v7c6x5z4a3s2d1f0g9h8j7k6l5', '系统管理员', '2025-01-01', 1, '管理员');

-- 2. 示例普通用户（密码：123456）
INSERT INTO `t_user` (`account`, `password`, `nickname`, `register_date`, `status`, `role`)
VALUES ('user001', '$2a$10$1a2s3d4f5g6h7j8k9l0z9x8c7v6b5n4m3l2k1j0h9g8f7d6s5a4s3d2f1g0h9j8k7l6', '普通用户001', '2025-01-02', 1, '普通用户');

-- 3. 示例游客用户
INSERT INTO `t_user` (`account`, `password`, `nickname`, `register_date`, `status`, `role`)
VALUES ('visitor001', '$2a$10$9z8x7c6v5b4n3m2l1k0j9h8g7f6d5s4a3s2d1f0g9h8j7k6l5m4n3b2v1c0x9z8y7x6w5', '游客001', '2025-01-03', 1, '游客');

-- 4. 初始化用户空间（管理员：10MB，普通用户/游客：5MB）
INSERT INTO `t_space` (`user_id`, `total_size`, `used_size`)
VALUES
    (1, 10485760, 0),  -- 管理员（user_id=1）：10MB
    (2, 5242880, 0),   -- 普通用户（user_id=2）：5MB
    (3, 5242880, 0);   -- 游客（user_id=3）：5MB

-- 5. 示例文件数据（普通用户上传的待审核图片，直接指定file_type）
INSERT INTO `t_file` (`user_id`, `file_type`, `file_name`, `file_path`, `file_size`, `status`)
VALUES (2, '图片', '风景图.jpg', '/static/images/scenery.jpg', 102400, '待审核');

-- 6. 示例关注关系（游客001关注普通用户001）
INSERT INTO `t_follow` (`user_id`, `follower_id`)
VALUES (2, 3);

-- 7. 示例审核记录（管理员审核普通用户文件）
INSERT INTO `t_audit_record` (`related_id`, `related_type`, `audit_user_id`, `audit_result`, `audit_remark`)
VALUES (1, 'file', 1, '待审核', '待人工审核图片合规性');
