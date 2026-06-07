-- Moli 后台管理系统 — 数据库结构（根据 moli-common 实体与 MyBatis-Plus 默认表名/字段命名推断生成）
-- 数据库名与 application-*.yml 中 spring.datasource.url 一致（默认 moli）
-- 字符集：utf8mb4；主键 ID 由应用侧 CustomIdGenerator / IdGenerator 赋值，未使用数据库自增

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ---------------------------------------------------------------------------
-- 可选：创建库（若已存在可注释掉下面两行）
-- ---------------------------------------------------------------------------
-- CREATE DATABASE IF NOT EXISTS `moli` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
-- USE `moli`;

-- ---------------------------------------------------------------------------
-- 系统模块
-- ---------------------------------------------------------------------------

DROP TABLE IF EXISTS `sys_user_role`;
DROP TABLE IF EXISTS `sys_role_menu`;
DROP TABLE IF EXISTS `sys_user_post`;
DROP TABLE IF EXISTS `sys_user`;
DROP TABLE IF EXISTS `sys_role`;
DROP TABLE IF EXISTS `sys_menu`;
DROP TABLE IF EXISTS `sys_post`;
DROP TABLE IF EXISTS `sys_dept`;
DROP TABLE IF EXISTS `sys_dict_data`;
DROP TABLE IF EXISTS `sys_dict_type`;
DROP TABLE IF EXISTS `sys_login_log`;
DROP TABLE IF EXISTS `sys_operation_log`;

CREATE TABLE `sys_dept` (
  `id`           BIGINT       NOT NULL COMMENT '主键',
  `create_id`    BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`    BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`  DATETIME     DEFAULT NULL COMMENT '修改时间',
  `parent_id`    BIGINT       DEFAULT NULL COMMENT '父级id',
  `dept_name`    VARCHAR(128) DEFAULT NULL COMMENT '部门名称',
  `order_num`    INT          DEFAULT NULL COMMENT '排序号',
  `status`       INT          DEFAULT NULL COMMENT '1:正常 0:停用',
  PRIMARY KEY (`id`),
  KEY `idx_sys_dept_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门';

CREATE TABLE `sys_user` (
  `id`            BIGINT        NOT NULL COMMENT '主键',
  `create_id`     BIGINT        DEFAULT NULL COMMENT '创建人',
  `create_time`   DATETIME      DEFAULT NULL COMMENT '创建时间',
  `update_id`     BIGINT        DEFAULT NULL COMMENT '修改人',
  `update_time`   DATETIME      DEFAULT NULL COMMENT '修改时间',
  `dept_id`       BIGINT        DEFAULT NULL COMMENT '部门ID',
  `work_no`       VARCHAR(64)   DEFAULT NULL COMMENT '工号',
  `nick_name`     VARCHAR(128)  DEFAULT NULL COMMENT '姓名',
  `user_name`     VARCHAR(128)  NOT NULL COMMENT '用户名',
  `password`      VARCHAR(256)  DEFAULT NULL COMMENT '密码',
  `identity_card` VARCHAR(32)   DEFAULT NULL COMMENT '身份证',
  `sex`           INT           DEFAULT NULL COMMENT '性别',
  `telephone`     VARCHAR(32)   DEFAULT NULL COMMENT '电话(业务上唯一)',
  `address`       VARCHAR(512)  DEFAULT NULL COMMENT '地址',
  `email`         VARCHAR(128)  DEFAULT NULL COMMENT '邮箱',
  `work_time`     DATETIME      DEFAULT NULL COMMENT '入职日期',
  `is_job`        INT           DEFAULT NULL COMMENT '是否在职 0在职 1离职',
  `status`        INT           DEFAULT NULL COMMENT '是否锁定 0未锁 1已锁',
  `error_num`     INT           DEFAULT NULL COMMENT '密码错误次数',
  `avatar`        VARCHAR(512)  DEFAULT NULL COMMENT '头像',
  `language`      VARCHAR(16)   DEFAULT 'zh-CN' COMMENT '界面语言 zh-CN/en-US/ja-JP',
  `salt`          VARCHAR(128)  DEFAULT NULL COMMENT '盐',
  `is_delete`     INT           DEFAULT 0 COMMENT '是否删除 0未删 1已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_user_name` (`user_name`),
  UNIQUE KEY `uk_sys_user_telephone` (`telephone`),
  KEY `idx_sys_user_dept_id` (`dept_id`),
  KEY `idx_sys_user_is_delete` (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户';

CREATE TABLE `sys_role` (
  `id`           BIGINT       NOT NULL COMMENT '主键',
  `create_id`    BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`    BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`  DATETIME     DEFAULT NULL COMMENT '修改时间',
  `role_name`    VARCHAR(128) DEFAULT NULL COMMENT '角色名称',
  `order_num`    VARCHAR(32)  DEFAULT NULL COMMENT '排序(实体为 String)',
  `remark`       VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `status`       INT          DEFAULT NULL COMMENT '1正常 0停用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色';

CREATE TABLE `sys_menu` (
  `id`           BIGINT       NOT NULL COMMENT '主键',
  `create_id`    BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`    BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`  DATETIME     DEFAULT NULL COMMENT '修改时间',
  `menu_name`    VARCHAR(128) DEFAULT NULL COMMENT '菜单名称',
  `menu_name_en` VARCHAR(128) DEFAULT NULL COMMENT '菜单名称(英文)',
  `menu_name_ja` VARCHAR(128) DEFAULT NULL COMMENT '菜单名称(日文)',
  `parent_id`    BIGINT       DEFAULT NULL COMMENT '父ID',
  `path`         VARCHAR(256) DEFAULT NULL COMMENT '路由',
  `component`    VARCHAR(256) DEFAULT NULL COMMENT '组件/路由名',
  `menu_type`    VARCHAR(8)   DEFAULT NULL COMMENT 'M目录 C菜单 F按钮',
  `perms`        VARCHAR(256) DEFAULT NULL COMMENT '权限标识',
  `status`       INT          DEFAULT NULL COMMENT '1启用 0禁用',
  `icon`         VARCHAR(128) DEFAULT NULL COMMENT '图标',
  `order_num`    INT          DEFAULT NULL COMMENT '显示顺序',
  PRIMARY KEY (`id`),
  KEY `idx_sys_menu_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单';

CREATE TABLE `sys_post` (
  `id`           BIGINT       NOT NULL COMMENT '主键',
  `create_id`    BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`    BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`  DATETIME     DEFAULT NULL COMMENT '修改时间',
  `post_code`    VARCHAR(64)  DEFAULT NULL COMMENT '岗位编码',
  `post_name`    VARCHAR(128) DEFAULT NULL COMMENT '岗位名称',
  `status`       INT          DEFAULT NULL COMMENT '1正常 0停用',
  `sort`         INT          DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_sys_post_post_code` (`post_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位';

CREATE TABLE `sys_user_role` (
  `id`      BIGINT NOT NULL COMMENT '主键',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `role_id` BIGINT DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `idx_sys_user_role_user_id` (`user_id`),
  KEY `idx_sys_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-角色';

CREATE TABLE `sys_role_menu` (
  `id`      BIGINT NOT NULL COMMENT '主键',
  `role_id` BIGINT DEFAULT NULL COMMENT '角色ID',
  `menu_id` BIGINT DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  KEY `idx_sys_role_menu_role_id` (`role_id`),
  KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色-菜单';

CREATE TABLE `sys_user_post` (
  `id`      BIGINT NOT NULL COMMENT '主键',
  `user_id` BIGINT DEFAULT NULL COMMENT '用户ID',
  `post_id` BIGINT DEFAULT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`),
  KEY `idx_sys_user_post_user_id` (`user_id`),
  KEY `idx_sys_user_post_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-岗位';

CREATE TABLE `sys_dict_type` (
  `id`           BIGINT       NOT NULL COMMENT '主键',
  `create_id`    BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`    BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`  DATETIME     DEFAULT NULL COMMENT '修改时间',
  `dict_name`    VARCHAR(128) DEFAULT NULL COMMENT '字典名称',
  `dict_type`    VARCHAR(128) DEFAULT NULL COMMENT '字典类型',
  `status`       INT          DEFAULT NULL COMMENT '1正常 0停用',
  `remark`       VARCHAR(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_dict_type_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型';

CREATE TABLE `sys_dict_data` (
  `id`           BIGINT       NOT NULL COMMENT '主键',
  `create_id`    BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`  DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`    BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`  DATETIME     DEFAULT NULL COMMENT '修改时间',
  `sort`         INT          DEFAULT NULL COMMENT '字典排序',
  `dict_key`     VARCHAR(128) DEFAULT NULL COMMENT '字典键',
  `dict_value`   VARCHAR(512) DEFAULT NULL COMMENT '字典值',
  `dict_value_en` VARCHAR(512) DEFAULT NULL COMMENT '字典值(英文)',
  `dict_value_ja` VARCHAR(512) DEFAULT NULL COMMENT '字典值(日文)',
  `dict_type`    VARCHAR(128) DEFAULT NULL COMMENT '字典类型',
  `status`       INT          DEFAULT NULL COMMENT '1正常 0停用',
  `remark`       VARCHAR(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_sys_dict_data_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据';

CREATE TABLE `sys_login_log` (
  `id`            BIGINT       NOT NULL COMMENT '主键',
  `user_name`     VARCHAR(128) DEFAULT NULL COMMENT '用户名',
  `ip_address`    VARCHAR(64)  DEFAULT NULL COMMENT 'IP',
  `login_address` VARCHAR(256) DEFAULT NULL COMMENT '登录地址',
  `browser`       VARCHAR(128) DEFAULT NULL COMMENT '浏览器',
  `os`            VARCHAR(128) DEFAULT NULL COMMENT '操作系统',
  `status`        INT          DEFAULT NULL COMMENT '1成功 0失败',
  `remark`        VARCHAR(512) DEFAULT NULL COMMENT '备注',
  `login_time`    DATETIME     DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_sys_login_log_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='登录日志';

CREATE TABLE `sys_operation_log` (
  `id`              BIGINT        NOT NULL COMMENT '主键',
  `title`           VARCHAR(256)  DEFAULT NULL COMMENT '标题',
  `business_type`   INT           DEFAULT NULL COMMENT '业务类型',
  `method_name`     VARCHAR(256)  DEFAULT NULL COMMENT '方法名',
  `request_method`  VARCHAR(16)   DEFAULT NULL COMMENT 'HTTP方法',
  `user_name`       VARCHAR(128)  DEFAULT NULL COMMENT '用户名',
  `request_ip`      VARCHAR(64)   DEFAULT NULL COMMENT '请求IP',
  `request_url`     VARCHAR(512)  DEFAULT NULL COMMENT 'URL',
  `request_param`   TEXT          COMMENT '请求参数',
  `response_result` TEXT          COMMENT '返回结果',
  `status`          INT           DEFAULT NULL COMMENT '1正常 0异常',
  `create_time`     DATETIME      DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sys_operation_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志';

-- ---------------------------------------------------------------------------
-- 运维/运营模块（实体存在；部分表当前无 Mapper，仍一并建表便于扩展）
-- ---------------------------------------------------------------------------

DROP TABLE IF EXISTS `operation_server_project`;
DROP TABLE IF EXISTS `operation_server_component`;
DROP TABLE IF EXISTS `operation_component_deploy_info`;
DROP TABLE IF EXISTS `operation_project_deploy_info`;
DROP TABLE IF EXISTS `operation_server_info`;
DROP TABLE IF EXISTS `operation_platform_info`;

CREATE TABLE `operation_platform_info` (
  `id`             BIGINT       NOT NULL COMMENT '主键',
  `create_id`      BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`    DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`      BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`    DATETIME     DEFAULT NULL COMMENT '修改时间',
  `platform_name`  VARCHAR(256) DEFAULT NULL COMMENT '平台名称',
  `url`            VARCHAR(512) DEFAULT NULL COMMENT 'URL',
  `account`        VARCHAR(256) DEFAULT NULL COMMENT '账户',
  `password`       VARCHAR(256) DEFAULT NULL COMMENT '密码(配置存储，勿写死密钥)',
  `environment`    INT          DEFAULT NULL COMMENT '环境 1dev 2test 3pre 4pro',
  `remark`         VARCHAR(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运营平台信息';

CREATE TABLE `operation_server_info` (
  `id`            BIGINT       NOT NULL COMMENT '主键',
  `create_id`     BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`   DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`     BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`   DATETIME     DEFAULT NULL COMMENT '修改时间',
  `server_name`   VARCHAR(256) DEFAULT NULL COMMENT '服务器名',
  `ip`            VARCHAR(64)  DEFAULT NULL COMMENT 'IP',
  `inner_ip`      VARCHAR(64)  DEFAULT NULL COMMENT '内网IP',
  `port`          VARCHAR(32)  DEFAULT NULL COMMENT '端口',
  `environment`   INT          DEFAULT NULL COMMENT '环境',
  `remark`        VARCHAR(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务器信息';

CREATE TABLE `operation_component_deploy_info` (
  `id`              BIGINT       NOT NULL COMMENT '主键',
  `create_id`       BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`     DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`       BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`     DATETIME     DEFAULT NULL COMMENT '修改时间',
  `component_name`  VARCHAR(256) DEFAULT NULL COMMENT '组件名',
  `server_ip`       VARCHAR(64)  DEFAULT NULL COMMENT '服务器IP',
  `account`         VARCHAR(256) DEFAULT NULL COMMENT '账户',
  `password`        VARCHAR(256) DEFAULT NULL COMMENT '密码',
  `deploy_path`     VARCHAR(512) DEFAULT NULL COMMENT '部署路径',
  `port`            VARCHAR(32)  DEFAULT NULL COMMENT '端口',
  `version`         VARCHAR(64)  DEFAULT NULL COMMENT '版本',
  `environment`     INT          DEFAULT NULL COMMENT '环境',
  `remark`          VARCHAR(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组件部署信息';

CREATE TABLE `operation_project_deploy_info` (
  `id`            BIGINT       NOT NULL COMMENT '主键',
  `create_id`     BIGINT       DEFAULT NULL COMMENT '创建人',
  `create_time`   DATETIME     DEFAULT NULL COMMENT '创建时间',
  `update_id`     BIGINT       DEFAULT NULL COMMENT '修改人',
  `update_time`   DATETIME     DEFAULT NULL COMMENT '修改时间',
  `server_id`     BIGINT       DEFAULT NULL COMMENT '服务器ID',
  `server_ip`     VARCHAR(64)  DEFAULT NULL COMMENT '服务器IP',
  `inner_ip`      VARCHAR(64)  DEFAULT NULL COMMENT '内网IP',
  `url`           VARCHAR(512) DEFAULT NULL COMMENT 'URL',
  `project_name`  VARCHAR(256) DEFAULT NULL COMMENT '项目名称',
  `deploy_path`   VARCHAR(512) DEFAULT NULL COMMENT '部署路径',
  `port`          VARCHAR(32)  DEFAULT NULL COMMENT '端口',
  `environment`   INT          DEFAULT NULL COMMENT '环境',
  `remark`        VARCHAR(512) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_operation_project_deploy_server_id` (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目部署信息';

CREATE TABLE `operation_server_project` (
  `id`         BIGINT NOT NULL COMMENT '主键(实体字段名为 Id，映射列为 id)',
  `server_id`  BIGINT DEFAULT NULL COMMENT '服务器ID',
  `project_id` BIGINT DEFAULT NULL COMMENT '项目ID',
  PRIMARY KEY (`id`),
  KEY `idx_operation_server_project_server_id` (`server_id`),
  KEY `idx_operation_server_project_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务器-项目关联';

CREATE TABLE `operation_server_component` (
  `id`           BIGINT NOT NULL COMMENT '主键',
  `server_id`    BIGINT DEFAULT NULL COMMENT '服务器ID',
  `component_id` BIGINT DEFAULT NULL COMMENT '组件ID',
  PRIMARY KEY (`id`),
  KEY `idx_operation_server_component_server_id` (`server_id`),
  KEY `idx_operation_server_component_component_id` (`component_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务器-组件关联';

SET FOREIGN_KEY_CHECKS = 1;
