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

 Date: 11/02/2022 11:15:48
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for task_instance_info
-- ----------------------------
DROP TABLE IF EXISTS `task_instance_info`;
CREATE TABLE `task_instance_info` (
  `id` bigint(20) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `task_code` varchar(20) COLLATE utf8_bin NOT NULL,
  `instance_params` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `parent_instance_id` int(20) DEFAULT NULL COMMENT '父任务实例ID',
  `actual_trigger_time` datetime(6) DEFAULT NULL,
  `expected_trigger_time` datetime(6) DEFAULT NULL,
  `running_times` int(11) unsigned zerofill DEFAULT NULL,
  `task_tracker_address` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT '任务执行的处理器地址',
  `type` varchar(10) COLLATE utf8_bin DEFAULT NULL,
  `result` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `finished_time` datetime(6) DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `status` int(1) unsigned zerofill NOT NULL COMMENT '0 等待执行 1 正在执行 2 执行成功 3 执行失败',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
