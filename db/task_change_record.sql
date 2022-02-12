/*
 Navicat Premium Data Transfer

 Source Server         : mac_mysql
 Source Server Type    : MySQL
 Source Server Version : 50720
 Source Host           : localhost:3306
 Source Schema         : rice

 Target Server Type    : MySQL
 Target Server Version : 50720
 File Encoding         : 65001

 Date: 11/02/2022 11:15:39
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for task_change_record
-- ----------------------------
DROP TABLE IF EXISTS `task_change_record`;
CREATE TABLE `task_change_record` (
  `id` bigint(20) unsigned zerofill NOT NULL AUTO_INCREMENT COMMENT '主键',
  `task_code` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '任务编码',
  `opt_type` int(1) NOT NULL COMMENT '操作类型:  0任务新增  1任务删除 2任务更新',
  `scheduler_server` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '托管调度器',
  `create_time` datetime(6) NOT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
