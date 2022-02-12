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

 Date: 11/02/2022 11:15:22
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rice_task_info
-- ----------------------------
DROP TABLE IF EXISTS `rice_task_info`;
CREATE TABLE `rice_task_info` (
  `id` bigint(20) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `app_id` bigint(20) NOT NULL,
  `task_code` varchar(20) COLLATE utf8_bin NOT NULL,
  `task_name` varchar(50) COLLATE utf8_bin NOT NULL,
  `task_desc` varchar(255) COLLATE utf8_bin NOT NULL,
  `task_type` int(1) NOT NULL COMMENT 'shell  python  java内置处理器',
  `parameters` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `scheduler_server` varchar(20) COLLATE utf8_bin NOT NULL,
  `schedule_type` varchar(20) COLLATE utf8_bin NOT NULL COMMENT 'CRON  固定频率 固定延迟 ',
  `time_expression` varchar(50) COLLATE utf8_bin NOT NULL,
  `execute_type` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '单机、广播  map  MapReduce  工作流',
  `threads` int(3) NOT NULL,
  `task_retry_count` int(3) unsigned zerofill NOT NULL COMMENT 'scheduler负责重试 ',
  `instance_retry_count` int(3) unsigned zerofill NOT NULL COMMENT '处理器负责重试',
  `next_trigger_time` datetime(6) DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `update_time` datetime(6) NOT NULL,
  `status` int(1) NOT NULL COMMENT '0 offline  1 online  2 pause',
  PRIMARY KEY (`id`,`task_code`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
