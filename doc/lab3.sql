create table if not exists frameworklab3.t_audit_record
(
    audit_id         bigint unsigned auto_increment comment '审核ID（自增主键）'
        primary key,
    related_id       bigint unsigned                    null comment '关联ID（file_id/）',
    related_space_id bigint unsigned                    null comment '空间',
    related_type     varchar(20)                        not null comment '关联类型：file（文件审核）/space（扩容审核）',
    audit_user_id    bigint unsigned                    not null comment '审核人ID（逻辑关联t_user）',
    audit_time       datetime default CURRENT_TIMESTAMP not null comment '审核时间',
    audit_result     varchar(20)                        not null comment '审核结果：通过/驳回/冻结（仅文件）',
    audit_reason     varchar(1024)                      null comment '驳回原因（可选）',
    audit_remark     varchar(1024)                      null comment '审核备注（如扩容额度、文件违规点）',
    create_time      datetime default CURRENT_TIMESTAMP not null
)
    comment '审核记录表（文件+空间扩容）';

create index idx_audit_time
    on frameworklab3.t_audit_record (audit_time)
    comment '审核时间索引';

create index idx_audit_user_id
    on frameworklab3.t_audit_record (audit_user_id)
    comment '审核人索引';

create index idx_related_id_type
    on frameworklab3.t_audit_record (related_id, related_type)
    comment '关联ID+类型索引';

create table if not exists frameworklab3.t_file
(
    file_id        bigint unsigned auto_increment comment '文件ID（自增主键）'
        primary key,
    user_id        bigint unsigned                    not null comment '所属用户ID（逻辑关联t_user）',
    file_type      varchar(20)                        not null comment '文件类型：文档/图片/相册/音乐',
    file_name      varchar(255)                       not null comment '文件名（支持模糊查询）',
    file_path      varchar(512)                       not null comment '文件相对路径（图片预览用）',
    file_size      bigint                             not null comment '文件大小（字节）',
    upload_time    datetime default CURRENT_TIMESTAMP not null comment '上传时间',
    status         int      default 0                 null comment '状态: 0正常/1冻结',
    download_count int      default 0                 not null comment '下载量',
    is_top         tinyint  default 0                 not null comment '是否置顶：1-是 0-否',
    create_time    datetime default CURRENT_TIMESTAMP not null,
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    review         int      default 0                 null,
    is_delete      int      default 0                 null
)
    comment '文件表（内置文件类型）';

create index idx_download_count
    on frameworklab3.t_file (download_count)
    comment '下载量索引';

create index idx_file_name
    on frameworklab3.t_file (file_name)
    comment '文件名索引';

create index idx_file_type
    on frameworklab3.t_file (file_type)
    comment '文件类型索引';

create index idx_user_id
    on frameworklab3.t_file (user_id)
    comment '用户ID索引';

create table if not exists frameworklab3.t_follow
(
    follow_id   bigint unsigned auto_increment comment '关注ID（自增主键）'
        primary key,
    user_id     bigint unsigned                    not null comment '被关注者ID（逻辑关联t_user）',
    follower_id bigint unsigned                    not null comment '关注者ID（逻辑关联t_user）',
    follow_time datetime default CURRENT_TIMESTAMP not null comment '关注时间',
    status      tinyint  default 1                 not null comment '状态：1-有效 0-取消',
    constraint uk_user_follower
        unique (user_id, follower_id) comment '避免重复关注'
)
    comment '用户关注表';

create index idx_follower_id
    on frameworklab3.t_follow (follower_id)
    comment '关注者索引';

create table if not exists frameworklab3.t_space
(
    space_id     bigint unsigned auto_increment comment '空间ID（自增主键）'
        primary key,
    user_id      bigint unsigned                       not null comment '所属用户ID（逻辑关联t_user）',
    total_size   bigint      default 5242880           not null comment '总容量（字节），默认5MB=5*1024*1024',
    used_size    bigint      default 0                 not null comment '已用容量（实时更新）',
    apply_size   bigint      default 0                 null comment '扩容申请容量（字节）',
    apply_status varchar(20) default '未申请'          null comment '申请状态：未申请/待审核/通过/驳回',
    apply_time   datetime                              null comment '扩容申请提交时间',
    audit_id     bigint unsigned                       null comment '关联审核记录ID（逻辑关联t_audit_record）',
    create_time  datetime    default CURRENT_TIMESTAMP not null,
    update_time  datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    constraint uk_user_id
        unique (user_id) comment '用户空间唯一'
)
    comment '用户空间表（扩容申请+容量管理）';

create index idx_apply_status
    on frameworklab3.t_space (apply_status)
    comment '申请状态索引';

create table if not exists frameworklab3.t_user
(
    user_id       bigint unsigned auto_increment comment '用户ID（自增主键）'
        primary key,
    account       varchar(50)                           not null comment '注册账号（唯一）',
    password      varchar(100)                          not null comment '加密密码（建议BCrypt加密）',
    nickname      varchar(50)                           not null comment '用户昵称',
    register_date date                                  not null comment '注册日期（支持yyyy-MM-dd/yyyy/MM/dd）',
    status        tinyint     default 1                 not null comment '状态：1-正常 0-冻结',
    role          varchar(20) default '普通用户'        not null comment '角色：管理员/普通用户/游客/系统用户',
    create_time   datetime    default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime    default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_account
        unique (account) comment '账号唯一约束'
)
    comment '用户表（含角色）';

create index idx_role
    on frameworklab3.t_user (role)
    comment '角色索引（按角色筛选用户）';

