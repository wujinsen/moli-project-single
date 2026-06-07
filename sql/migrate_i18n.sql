-- i18n migration: run once on existing database
SET NAMES utf8mb4;

ALTER TABLE `sys_menu`
  ADD COLUMN `menu_name_en` VARCHAR(128) DEFAULT NULL COMMENT 'menu name en' AFTER `menu_name`,
  ADD COLUMN `menu_name_ja` VARCHAR(128) DEFAULT NULL COMMENT 'menu name ja' AFTER `menu_name_en`;

ALTER TABLE `sys_user`
  ADD COLUMN `language` VARCHAR(16) DEFAULT 'zh-CN' COMMENT 'ui language' AFTER `avatar`;

ALTER TABLE `sys_dict_data`
  ADD COLUMN `dict_value_en` VARCHAR(512) DEFAULT NULL COMMENT 'dict value en' AFTER `dict_value`,
  ADD COLUMN `dict_value_ja` VARCHAR(512) DEFAULT NULL COMMENT 'dict value ja' AFTER `dict_value_en`;

UPDATE `sys_user` SET `language` = 'zh-CN' WHERE `language` IS NULL OR `language` = '';
