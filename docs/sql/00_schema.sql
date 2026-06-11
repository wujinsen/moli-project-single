-- Moli database schema (exported 2026-06-11)
-- Database: moli
-- Run before 01_baseline_data.sql on empty database.

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `operation_component_deploy_info`;
CREATE TABLE `operation_component_deploy_info` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `component_name` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件名',
  `server_ip` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '服务器IP',
  `account` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '账户',
  `password` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `deploy_path` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部署路径',
  `port` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '端口',
  `version` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '版本',
  `environment` int DEFAULT NULL COMMENT '环境',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组件部署信息';

DROP TABLE IF EXISTS `operation_platform_info`;
CREATE TABLE `operation_platform_info` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `platform_name` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '平台名称',
  `url` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'URL',
  `account` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '账户',
  `password` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码(配置存储，勿写死密钥)',
  `environment` int DEFAULT NULL COMMENT '环境 1dev 2test 3pre 4pro',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='运营平台信息';

DROP TABLE IF EXISTS `operation_project_deploy_info`;
CREATE TABLE `operation_project_deploy_info` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `server_id` bigint DEFAULT NULL COMMENT '服务器ID',
  `server_ip` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '服务器IP',
  `inner_ip` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '内网IP',
  `url` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'URL',
  `project_name` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '项目名称',
  `deploy_path` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部署路径',
  `port` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '端口',
  `environment` int DEFAULT NULL COMMENT '环境',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_operation_project_deploy_server_id` (`server_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='项目部署信息';

DROP TABLE IF EXISTS `operation_server_component`;
CREATE TABLE `operation_server_component` (
  `id` bigint NOT NULL COMMENT '主键',
  `server_id` bigint DEFAULT NULL COMMENT '服务器ID',
  `component_id` bigint DEFAULT NULL COMMENT '组件ID',
  PRIMARY KEY (`id`),
  KEY `idx_operation_server_component_server_id` (`server_id`),
  KEY `idx_operation_server_component_component_id` (`component_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务器-组件关联';

DROP TABLE IF EXISTS `operation_server_info`;
CREATE TABLE `operation_server_info` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `server_name` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '服务器名',
  `ip` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'IP',
  `inner_ip` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '内网IP',
  `port` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '端口',
  `environment` int DEFAULT NULL COMMENT '环境',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务器信息';

DROP TABLE IF EXISTS `operation_server_project`;
CREATE TABLE `operation_server_project` (
  `id` bigint NOT NULL COMMENT '主键(实体字段名为 Id，映射列为 id)',
  `server_id` bigint DEFAULT NULL COMMENT '服务器ID',
  `project_id` bigint DEFAULT NULL COMMENT '项目ID',
  PRIMARY KEY (`id`),
  KEY `idx_operation_server_project_server_id` (`server_id`),
  KEY `idx_operation_server_project_project_id` (`project_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='服务器-项目关联';

DROP TABLE IF EXISTS `sys_action`;
CREATE TABLE `sys_action` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `perm_code` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限码，全局唯一',
  `resource` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '资源，如 user',
  `action` varchar(32) COLLATE utf8mb4_general_ci NOT NULL COMMENT '动作，如 add',
  `name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '显示名称',
  `menu_id` bigint DEFAULT NULL COMMENT '关联 C 页面，仅 UI 分组',
  `order_num` int DEFAULT '0',
  `status` tinyint DEFAULT '1' COMMENT '1启用 0停用',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`perm_code`),
  KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=341 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统动作目录（非导航）';

DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `parent_id` bigint DEFAULT NULL COMMENT '父级id',
  `dept_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '部门名称',
  `order_num` int DEFAULT NULL COMMENT '排序号',
  `status` int DEFAULT NULL COMMENT '1:正常 0:停用',
  PRIMARY KEY (`id`),
  KEY `idx_sys_dept_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门';

DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `sort` int DEFAULT NULL COMMENT '字典排序',
  `dict_key` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典键',
  `dict_value` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典值',
  `dict_value_en` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典值(英文)',
  `dict_value_ja` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典值(日文)',
  `dict_type` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典类型',
  `status` int DEFAULT NULL COMMENT '1正常 0停用',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `idx_sys_dict_data_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据';

DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `dict_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典名称',
  `dict_type` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '字典类型',
  `status` int DEFAULT NULL COMMENT '1正常 0停用',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_dict_type_dict_type` (`dict_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型';

DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
  `ip_address` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'IP',
  `login_address` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '登录地址',
  `browser` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '浏览器',
  `os` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '操作系统',
  `status` int DEFAULT NULL COMMENT '1成功 0失败',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`),
  KEY `idx_sys_login_log_login_time` (`login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='登录日志';

DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `menu_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单名称',
  `menu_name_en` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单名称(英文)',
  `menu_name_ja` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '菜单名称(日文)',
  `parent_id` bigint DEFAULT NULL COMMENT '父ID',
  `path` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由',
  `component` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件/路由名',
  `route_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Vue路由名称(name)',
  `menu_type` varchar(8) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'M目录 C菜单 F按钮',
  `perms` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
  `status` int DEFAULT NULL COMMENT '1启用 0禁用',
  `icon` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图标',
  `order_num` int DEFAULT NULL COMMENT '显示顺序',
  PRIMARY KEY (`id`),
  KEY `idx_sys_menu_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单';

DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log` (
  `id` bigint NOT NULL COMMENT '主键',
  `title` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '标题',
  `business_type` int DEFAULT NULL COMMENT '业务类型',
  `method_name` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '方法名',
  `request_method` varchar(16) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'HTTP方法',
  `user_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '用户名',
  `request_ip` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '请求IP',
  `request_url` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'URL',
  `request_param` text COLLATE utf8mb4_general_ci COMMENT '请求参数',
  `response_result` text COLLATE utf8mb4_general_ci COMMENT '返回结果',
  `status` int DEFAULT NULL COMMENT '1正常 0异常',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_sys_operation_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志';

DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `post_code` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '岗位编码',
  `post_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '岗位名称',
  `status` int DEFAULT NULL COMMENT '1正常 0停用',
  `sort` int DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`),
  KEY `idx_sys_post_post_code` (`post_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位';

DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `role_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '角色名称',
  `order_num` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '排序(实体为 String)',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `status` int DEFAULT NULL COMMENT '1正常 0停用',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色';

DROP TABLE IF EXISTS `sys_role_action`;
CREATE TABLE `sys_role_action` (
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `perm_code` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '权限码',
  PRIMARY KEY (`role_id`,`perm_code`),
  KEY `idx_perm_code` (`perm_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色-动作权限';

DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `id` bigint NOT NULL COMMENT '主键',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`),
  KEY `idx_sys_role_menu_role_id` (`role_id`),
  KEY `idx_sys_role_menu_menu_id` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色-菜单';

DROP TABLE IF EXISTS `sys_system`;
CREATE TABLE `sys_system` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `system_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统编码',
  `system_name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统名称',
  `base_url` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '访问根 URL',
  `icon` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '图标',
  `sort` int DEFAULT '0' COMMENT '排序',
  `status` int DEFAULT '1' COMMENT '1启用 0停用',
  `sso_mode` varchar(16) COLLATE utf8mb4_general_ci DEFAULT 'INTERNAL' COMMENT 'INTERNAL/EXTERNAL',
  `entry_path` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '/sso/login' COMMENT 'SSO 入口路径',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `system_group` varchar(32) COLLATE utf8mb4_general_ci DEFAULT 'business' COMMENT '门户分组',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_system_code` (`system_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='业务系统';

DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint DEFAULT NULL COMMENT '修改人',
  `update_time` datetime DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint DEFAULT NULL COMMENT '部门ID',
  `work_no` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '工号',
  `nick_name` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '姓名',
  `user_name` varchar(128) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(256) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '密码',
  `identity_card` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '身份证',
  `sex` int DEFAULT NULL COMMENT '性别',
  `telephone` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '电话(业务上唯一)',
  `address` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '地址',
  `email` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `work_time` datetime DEFAULT NULL COMMENT '入职日期',
  `is_job` int DEFAULT NULL COMMENT '是否在职 0在职 1离职',
  `status` int DEFAULT NULL COMMENT '是否锁定 0未锁 1已锁',
  `error_num` int DEFAULT NULL COMMENT '密码错误次数',
  `avatar` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '头像',
  `language` varchar(16) COLLATE utf8mb4_general_ci DEFAULT 'zh-CN' COMMENT '界面语言',
  `salt` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '盐',
  `is_delete` int DEFAULT '0' COMMENT '是否删除 0未删 1已删',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_user_user_name` (`user_name`),
  UNIQUE KEY `uk_sys_user_telephone` (`telephone`),
  KEY `idx_sys_user_dept_id` (`dept_id`),
  KEY `idx_sys_user_is_delete` (`is_delete`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户';

DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `post_id` bigint DEFAULT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`),
  KEY `idx_sys_user_post_user_id` (`user_id`),
  KEY `idx_sys_user_post_post_id` (`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-岗位';

DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`),
  KEY `idx_sys_user_role_user_id` (`user_id`),
  KEY `idx_sys_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-角色';

DROP TABLE IF EXISTS `sys_user_system`;
CREATE TABLE `sys_user_system` (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `system_id` bigint NOT NULL COMMENT '系统ID',
  `is_default` int DEFAULT '0' COMMENT '1默认 0否',
  PRIMARY KEY (`id`),
  KEY `idx_sys_user_system_user_id` (`user_id`),
  KEY `idx_sys_user_system_system_id` (`system_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户-系统';

SET FOREIGN_KEY_CHECKS = 1;
