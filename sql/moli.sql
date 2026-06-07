/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80046 (8.0.46)
 Source Host           : localhost:3306
 Source Schema         : moli

 Target Server Type    : MySQL
 Target Server Version : 80046 (8.0.46)
 File Encoding         : 65001

 Date: 08/06/2026 00:07:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for operation_component_deploy_info
-- ----------------------------
DROP TABLE IF EXISTS `operation_component_deploy_info`;
CREATE TABLE `operation_component_deploy_info`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `component_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件名',
  `server_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器IP',
  `account` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户',
  `password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `deploy_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部署路径',
  `port` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '端口',
  `version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '版本',
  `environment` int NULL DEFAULT NULL COMMENT '环境',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '组件部署信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_component_deploy_info
-- ----------------------------

-- ----------------------------
-- Table structure for operation_platform_info
-- ----------------------------
DROP TABLE IF EXISTS `operation_platform_info`;
CREATE TABLE `operation_platform_info`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `platform_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '平台名称',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'URL',
  `account` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '账户',
  `password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码(配置存储，勿写死密钥)',
  `environment` int NULL DEFAULT NULL COMMENT '环境 1dev 2test 3pre 4pro',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '运营平台信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_platform_info
-- ----------------------------

-- ----------------------------
-- Table structure for operation_project_deploy_info
-- ----------------------------
DROP TABLE IF EXISTS `operation_project_deploy_info`;
CREATE TABLE `operation_project_deploy_info`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `server_id` bigint NULL DEFAULT NULL COMMENT '服务器ID',
  `server_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器IP',
  `inner_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内网IP',
  `url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'URL',
  `project_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '项目名称',
  `deploy_path` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部署路径',
  `port` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '端口',
  `environment` int NULL DEFAULT NULL COMMENT '环境',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_project_deploy_server_id`(`server_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '项目部署信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_project_deploy_info
-- ----------------------------

-- ----------------------------
-- Table structure for operation_server_component
-- ----------------------------
DROP TABLE IF EXISTS `operation_server_component`;
CREATE TABLE `operation_server_component`  (
  `id` bigint NOT NULL COMMENT '主键',
  `server_id` bigint NULL DEFAULT NULL COMMENT '服务器ID',
  `component_id` bigint NULL DEFAULT NULL COMMENT '组件ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_server_component_server_id`(`server_id` ASC) USING BTREE,
  INDEX `idx_operation_server_component_component_id`(`component_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务器-组件关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_server_component
-- ----------------------------

-- ----------------------------
-- Table structure for operation_server_info
-- ----------------------------
DROP TABLE IF EXISTS `operation_server_info`;
CREATE TABLE `operation_server_info`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `server_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '服务器名',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP',
  `inner_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '内网IP',
  `port` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '端口',
  `environment` int NULL DEFAULT NULL COMMENT '环境',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务器信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_server_info
-- ----------------------------

-- ----------------------------
-- Table structure for operation_server_project
-- ----------------------------
DROP TABLE IF EXISTS `operation_server_project`;
CREATE TABLE `operation_server_project`  (
  `id` bigint NOT NULL COMMENT '主键(实体字段名为 Id，映射列为 id)',
  `server_id` bigint NULL DEFAULT NULL COMMENT '服务器ID',
  `project_id` bigint NULL DEFAULT NULL COMMENT '项目ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_operation_server_project_server_id`(`server_id` ASC) USING BTREE,
  INDEX `idx_operation_server_project_project_id`(`project_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '服务器-项目关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of operation_server_project
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父级id',
  `dept_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `order_num` int NULL DEFAULT NULL COMMENT '排序号',
  `status` int NULL DEFAULT NULL COMMENT '1:正常 0:停用',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_dept_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '部门' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dept
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `sort` int NULL DEFAULT NULL COMMENT '字典排序',
  `dict_key` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典键',
  `dict_value` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典值',
  `dict_value_en` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典值(英文)',
  `dict_value_ja` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典值(日文)',
  `dict_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `status` int NULL DEFAULT NULL COMMENT '1正常 0停用',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_dict_data_dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `dict_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典名称',
  `dict_type` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '字典类型',
  `status` int NULL DEFAULT NULL COMMENT '1正常 0停用',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_dict_type_dict_type`(`dict_type` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '字典类型' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
  `id` bigint NOT NULL COMMENT '主键',
  `user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `ip_address` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'IP',
  `login_address` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '登录地址',
  `browser` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '浏览器',
  `os` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作系统',
  `status` int NULL DEFAULT NULL COMMENT '1成功 0失败',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `login_time` datetime NULL DEFAULT NULL COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_login_log_login_time`(`login_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '登录日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_login_log
-- ----------------------------
INSERT INTO `sys_login_log` VALUES (715223136441729024, NULL, '127.0.0.1', NULL, NULL, NULL, 0, '用户不存在或者密码错误', '2026-05-28 15:21:24');
INSERT INTO `sys_login_log` VALUES (718870412062097408, NULL, '127.0.0.1', NULL, NULL, NULL, 0, '用户认证失败', '2026-06-07 16:54:22');
INSERT INTO `sys_login_log` VALUES (718870969086640128, NULL, '127.0.0.1', NULL, NULL, NULL, 0, '用户认证失败', '2026-06-07 16:56:35');
INSERT INTO `sys_login_log` VALUES (718871015769243648, NULL, '127.0.0.1', NULL, NULL, NULL, 0, '用户认证失败', '2026-06-07 16:56:46');
INSERT INTO `sys_login_log` VALUES (718874590452908032, NULL, '127.0.0.1', NULL, NULL, NULL, 0, '用户认证失败', '2026-06-07 17:10:58');
INSERT INTO `sys_login_log` VALUES (718874629866782720, NULL, '127.0.0.1', NULL, NULL, NULL, 0, '用户认证失败', '2026-06-07 17:11:08');
INSERT INTO `sys_login_log` VALUES (718874949590188032, NULL, '127.0.0.1', NULL, NULL, NULL, 0, '用户认证失败', '2026-06-07 17:12:24');
INSERT INTO `sys_login_log` VALUES (718874992170762240, NULL, '127.0.0.1', NULL, NULL, NULL, 0, '用户认证失败', '2026-06-07 17:12:34');
INSERT INTO `sys_login_log` VALUES (718875330034532352, 'admin', '127.0.0.1', NULL, NULL, NULL, 1, '登录成功', '2026-06-07 17:13:55');
INSERT INTO `sys_login_log` VALUES (718875926267428864, 'admin', '127.0.0.1', NULL, NULL, NULL, 1, '登录成功', '2026-06-07 17:16:17');
INSERT INTO `sys_login_log` VALUES (718896388439539712, 'admin', '127.0.0.1', NULL, NULL, NULL, 1, '登录成功', '2026-06-07 18:37:35');
INSERT INTO `sys_login_log` VALUES (718903966020141056, 'admin', '127.0.0.1', NULL, NULL, NULL, 1, '登录成功', '2026-06-07 19:07:42');
INSERT INTO `sys_login_log` VALUES (718912812411256832, 'admin', '127.0.0.1', NULL, NULL, NULL, 1, '登录成功', '2026-06-07 19:42:51');
INSERT INTO `sys_login_log` VALUES (718913083023556608, 'admin', '127.0.0.1', NULL, NULL, NULL, 1, '登录成功', '2026-06-07 19:43:56');
INSERT INTO `sys_login_log` VALUES (718916494523629568, 'admin', '127.0.0.1', NULL, NULL, NULL, 1, '登录成功', '2026-06-07 19:57:29');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `menu_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名称',
  `menu_name_en` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名称(英文)',
  `menu_name_ja` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '菜单名称(日文)',
  `parent_id` bigint NULL DEFAULT NULL COMMENT '父ID',
  `path` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '路由',
  `component` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '组件/路由名',
  `menu_type` varchar(8) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'M目录 C菜单 F按钮',
  `perms` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `status` int NULL DEFAULT NULL COMMENT '1启用 0禁用',
  `icon` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '图标',
  `order_num` int NULL DEFAULT NULL COMMENT '显示顺序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_menu_parent_id`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '菜单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES (1, NULL, NULL, NULL, NULL, '系统管理', 'System', 'システム', 0, 'system', NULL, 'M', NULL, 1, 'system', 1);
INSERT INTO `sys_menu` VALUES (2, NULL, NULL, NULL, NULL, '用户管理', 'Users', 'ユーザー管理', 1, 'user', 'system/user/index', 'C', 'system:user:list', 1, 'user', 1);
INSERT INTO `sys_menu` VALUES (3, NULL, NULL, NULL, NULL, '角色管理', 'Roles', 'ロール管理', 1, 'role', 'system/role/index', 'C', 'system:role:list', 1, 'peoples', 2);
INSERT INTO `sys_menu` VALUES (4, NULL, NULL, NULL, NULL, '菜单管理', 'Menus', 'メニュー管理', 1, 'menu', 'system/menu/index', 'C', 'system:menu:list', 1, 'tree-table', 3);
INSERT INTO `sys_menu` VALUES (5, NULL, NULL, NULL, NULL, '部门管理', 'Departments', '部署管理', 1, 'dept', 'system/dept/index', 'C', 'system:dept:list', 1, 'tree', 4);
INSERT INTO `sys_menu` VALUES (6, NULL, NULL, NULL, NULL, '岗位管理', 'Posts', 'ポスト管理', 1, 'post', 'system/post/index', 'C', 'system:post:list', 1, 'post', 5);
INSERT INTO `sys_menu` VALUES (7, NULL, NULL, NULL, NULL, '字典管理', 'Dictionary', '辞書管理', 1, 'dict', 'system/dict/index', 'C', 'system:dict:list', 1, 'dict', 6);
INSERT INTO `sys_menu` VALUES (8, NULL, NULL, NULL, NULL, '参数设置', 'Parameters', 'パラメータ', 1, 'config', 'system/config/index', 'C', 'system:config:list', 1, 'edit', 7);
INSERT INTO `sys_menu` VALUES (9, NULL, NULL, NULL, NULL, '通知公告', 'Notices', 'お知らせ', 1, 'notice', 'system/notice/index', 'C', 'system:notice:list', 1, 'message', 8);
INSERT INTO `sys_menu` VALUES (400, NULL, NULL, NULL, NULL, '运营管理', 'Operations', '運用管理', 0, 'operation', NULL, 'M', NULL, 1, 'guide', 2);
INSERT INTO `sys_menu` VALUES (401, NULL, NULL, NULL, NULL, '项目管理', 'Projects', 'プロジェクト', 400, 'project', 'operation/project/index', 'C', 'operation:project:list', 1, 'example', 1);
INSERT INTO `sys_menu` VALUES (402, NULL, NULL, NULL, NULL, '服务器管理', 'Servers', 'サーバー', 400, 'server', 'operation/server/index', 'C', 'operation:server:list', 1, 'server', 2);
INSERT INTO `sys_menu` VALUES (403, NULL, NULL, NULL, NULL, '平台管理', 'Platforms', 'プラットフォーム', 400, 'platform', 'operation/platform/index', 'C', 'operation:platform:list', 1, 'tree', 3);
INSERT INTO `sys_menu` VALUES (404, NULL, NULL, NULL, NULL, '组件管理', 'Components', 'コンポーネント', 400, 'component', 'operation/component/index', 'C', 'operation:component:list', 1, 'component', 4);
INSERT INTO `sys_menu` VALUES (500, NULL, NULL, NULL, NULL, 'ChatGPT', 'ChatGPT', 'ChatGPT', 0, 'chatgpt', NULL, 'M', NULL, 1, 'message', 3);
INSERT INTO `sys_menu` VALUES (501, NULL, NULL, NULL, NULL, '智能对话', 'AI Chat', 'AI対話', 500, 'completion', 'chatgpt/completion/index', 'C', 'chatgpt:completion:list', 1, 'message', 1);
INSERT INTO `sys_menu` VALUES (600, NULL, NULL, NULL, NULL, '烛龙数据', 'Candlelight', '燭龍データ', 0, 'candlelight', NULL, 'M', NULL, 1, 'chart', 4);
INSERT INTO `sys_menu` VALUES (601, NULL, NULL, NULL, NULL, 'BI分析', 'BI', 'BI分析', 600, 'bi', 'CandlelightDragon/bi/index', 'C', 'candlelight:bi:list', 1, 'chart', 1);
INSERT INTO `sys_menu` VALUES (602, NULL, NULL, NULL, NULL, '数据驾驶舱', 'Cockpit', 'コックピット', 600, 'cockpit', 'CandlelightDragon/cockpit/index', 'C', 'candlelight:cockpit:list', 1, 'dashboard', 2);
INSERT INTO `sys_menu` VALUES (603, NULL, NULL, NULL, NULL, '用户画像', 'User Portrait', 'ユーザーピクチャ', 600, 'userportrait', 'CandlelightDragon/userportrait/index', 'C', 'candlelight:userportrait:list', 1, 'user', 3);

-- ----------------------------
-- Table structure for sys_operation_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_operation_log`;
CREATE TABLE `sys_operation_log`  (
  `id` bigint NOT NULL COMMENT '主键',
  `title` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '标题',
  `business_type` int NULL DEFAULT NULL COMMENT '业务类型',
  `method_name` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名',
  `request_method` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'HTTP方法',
  `user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '用户名',
  `request_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求IP',
  `request_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'URL',
  `request_param` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '请求参数',
  `response_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '返回结果',
  `status` int NULL DEFAULT NULL COMMENT '1正常 0异常',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_operation_log_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '操作日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_operation_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `post_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位编码',
  `post_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '岗位名称',
  `status` int NULL DEFAULT NULL COMMENT '1正常 0停用',
  `sort` int NULL DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_post_post_code`(`post_code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '岗位' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_post
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `role_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '角色名称',
  `order_num` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '排序(实体为 String)',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NULL DEFAULT NULL COMMENT '1正常 0停用',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role
-- ----------------------------

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `id` bigint NOT NULL COMMENT '主键',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID',
  `menu_id` bigint NULL DEFAULT NULL COMMENT '菜单ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_role_menu_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_sys_role_menu_menu_id`(`menu_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色-菜单' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES (10001, 1, 1);
INSERT INTO `sys_role_menu` VALUES (10002, 1, 2);
INSERT INTO `sys_role_menu` VALUES (10003, 1, 3);
INSERT INTO `sys_role_menu` VALUES (10004, 1, 4);
INSERT INTO `sys_role_menu` VALUES (10005, 1, 5);
INSERT INTO `sys_role_menu` VALUES (10006, 1, 6);
INSERT INTO `sys_role_menu` VALUES (10007, 1, 7);
INSERT INTO `sys_role_menu` VALUES (10008, 1, 8);
INSERT INTO `sys_role_menu` VALUES (10009, 1, 9);
INSERT INTO `sys_role_menu` VALUES (10400, 1, 400);
INSERT INTO `sys_role_menu` VALUES (10401, 1, 401);
INSERT INTO `sys_role_menu` VALUES (10402, 1, 402);
INSERT INTO `sys_role_menu` VALUES (10403, 1, 403);
INSERT INTO `sys_role_menu` VALUES (10404, 1, 404);
INSERT INTO `sys_role_menu` VALUES (10500, 1, 500);
INSERT INTO `sys_role_menu` VALUES (10501, 1, 501);
INSERT INTO `sys_role_menu` VALUES (10600, 1, 600);
INSERT INTO `sys_role_menu` VALUES (10601, 1, 601);
INSERT INTO `sys_role_menu` VALUES (10602, 1, 602);
INSERT INTO `sys_role_menu` VALUES (10603, 1, 603);

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL COMMENT '主键',
  `create_id` bigint NULL DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `update_id` bigint NULL DEFAULT NULL COMMENT '修改人',
  `update_time` datetime NULL DEFAULT NULL COMMENT '修改时间',
  `dept_id` bigint NULL DEFAULT NULL COMMENT '部门ID',
  `work_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '工号',
  `nick_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '姓名',
  `user_name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '密码',
  `identity_card` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '身份证',
  `sex` int NULL DEFAULT NULL COMMENT '性别',
  `telephone` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '电话(业务上唯一)',
  `address` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '地址',
  `email` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `work_time` datetime NULL DEFAULT NULL COMMENT '入职日期',
  `is_job` int NULL DEFAULT NULL COMMENT '是否在职 0在职 1离职',
  `status` int NULL DEFAULT NULL COMMENT '是否锁定 0未锁 1已锁',
  `error_num` int NULL DEFAULT NULL COMMENT '密码错误次数',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '头像',
  `language` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT 'zh-CN' COMMENT '界面语言',
  `salt` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '盐',
  `is_delete` int NULL DEFAULT 0 COMMENT '是否删除 0未删 1已删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_sys_user_user_name`(`user_name` ASC) USING BTREE,
  UNIQUE INDEX `uk_sys_user_telephone`(`telephone` ASC) USING BTREE,
  INDEX `idx_sys_user_dept_id`(`dept_id` ASC) USING BTREE,
  INDEX `idx_sys_user_is_delete`(`is_delete` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES (1, NULL, NULL, 1, '2026-06-07 19:58:48', NULL, NULL, NULL, 'admin', 'a7917efb0e543c470f9a78a12e73f7d7802e589f4133cc83e29b83d54efef169', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'zh-CN', 'moli', 0);

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post`  (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `post_id` bigint NULL DEFAULT NULL COMMENT '岗位ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_user_post_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_sys_user_post_post_id`(`post_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户-岗位' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `id` bigint NOT NULL COMMENT '主键',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `role_id` bigint NULL DEFAULT NULL COMMENT '角色ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_user_role_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_sys_user_role_role_id`(`role_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户-角色' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
