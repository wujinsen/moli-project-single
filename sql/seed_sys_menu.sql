-- moli �˵���ʼ���ű�������� + ���
-- ������ϵͳ��������Ӫ������ChatGPT����������
-- ������ϵͳ��ء�ϵͳ���ߡ���ťȨ��(F)

SET NAMES utf8mb4;

DELETE FROM `sys_role_menu`;
DELETE FROM `sys_menu`;

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_name_en`, `menu_name_ja`, `parent_id`, `path`, `component`, `menu_type`, `perms`, `status`, `icon`, `order_num`) VALUES
(1,   'ϵͳ����', 'System',           '�����ƥ�',       0, 'system',      NULL, 'M', NULL, 1, 'system',  1),
(400, '��Ӫ����', 'Operations',       '�\�ù���',       0, 'operation',   NULL, 'M', NULL, 1, 'guide',   2),
(500, 'ChatGPT',  'ChatGPT',          'ChatGPT',        0, 'chatgpt',     NULL, 'M', NULL, 1, 'message', 3),
(600, '��������', 'Candlelight',      '�T���ǩ`��',     0, 'candlelight', NULL, 'M', NULL, 1, 'chart',   4);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_name_en`, `menu_name_ja`, `parent_id`, `path`, `component`, `menu_type`, `perms`, `status`, `icon`, `order_num`) VALUES
(2, '�û�����', 'Users',        '��`���`����', 1, 'user',   'system/user/index',   'C', 'system:user:list',   1, 'user',       1),
(3, '��ɫ����', 'Roles',        '���`�����',   1, 'role',   'system/role/index',   'C', 'system:role:list',   1, 'peoples',    2),
(4, '�˵�����', 'Menus',        '��˥�`����', 1, 'menu',   'system/menu/index',   'C', 'system:menu:list',   1, 'tree-table', 3),
(5, '���Ź���', 'Departments',  '�������',     1, 'dept',   'system/dept/index',   'C', 'system:dept:list',   1, 'tree',       4),
(6, '��λ����', 'Posts',        '�ݥ��ȹ���',   1, 'post',   'system/post/index',   'C', 'system:post:list',   1, 'post',       5),
(7, '�ֵ����', 'Dictionary',   '�Ǖ�����',     1, 'dict',   'system/dict/index',   'C', 'system:dict:list',   1, 'dict',       6),
(8, '��������', 'Parameters',   '�ѥ��`��',   1, 'config', 'system/config/index', 'C', 'system:config:list', 1, 'edit',       7),
(9, '֪ͨ����', 'Notices',      '��֪�餻',     1, 'notice', 'system/notice/index', 'C', 'system:notice:list', 1, 'message',    8);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_name_en`, `menu_name_ja`, `parent_id`, `path`, `component`, `menu_type`, `perms`, `status`, `icon`, `order_num`) VALUES
(401, '��Ŀ����',   'Projects',   '�ץ���������',     400, 'project',   'operation/project/index',   'C', 'operation:project:list',   1, 'example',   1),
(402, '����������', 'Servers',    '���`�Щ`',         400, 'server',    'operation/server/index',    'C', 'operation:server:list',    1, 'server',    2),
(403, 'ƽ̨����',   'Platforms',  '�ץ�åȥե��`��', 400, 'platform',  'operation/platform/index',  'C', 'operation:platform:list',  1, 'tree',      3),
(404, '�������',   'Components', '����ݩ`�ͥ��',   400, 'component', 'operation/component/index', 'C', 'operation:component:list', 1, 'component', 4);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_name_en`, `menu_name_ja`, `parent_id`, `path`, `component`, `menu_type`, `perms`, `status`, `icon`, `order_num`) VALUES
(501, '���ܶԻ�', 'AI Chat', 'AI��Ԓ', 500, 'completion', 'chatgpt/completion/index', 'C', 'chatgpt:completion:list', 1, 'message', 1);

INSERT INTO `sys_menu` (`id`, `menu_name`, `menu_name_en`, `menu_name_ja`, `parent_id`, `path`, `component`, `menu_type`, `perms`, `status`, `icon`, `order_num`) VALUES
(601, 'BI����',     'BI',            'BI����',       600, 'bi',           'CandlelightDragon/bi/index',           'C', 'candlelight:bi:list',           1, 'chart',     1),
(602, '���ݼ�ʻ��', 'Cockpit',       '���å��ԥå�', 600, 'cockpit',      'CandlelightDragon/cockpit/index',      'C', 'candlelight:cockpit:list',      1, 'dashboard', 2),
(603, '�û�����',   'User Portrait', '��`���`�ԥ�����', 600, 'userportrait', 'CandlelightDragon/userportrait/index', 'C', 'candlelight:userportrait:list', 1, 'user',      3);

INSERT INTO `sys_role_menu` (`id`, `role_id`, `menu_id`)
SELECT (`id` + 10000), 1, `id` FROM `sys_menu`;
