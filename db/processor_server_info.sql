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

 Date: 11/02/2022 11:14:12
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for processor_server_info
-- ----------------------------
DROP TABLE IF EXISTS `processor_server_info`;
CREATE TABLE `processor_server_info` (
  `id` bigint(20) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `address` varchar(20) COLLATE utf8_bin NOT NULL,
  `port` int(8) NOT NULL,
  `task_code` varchar(20) COLLATE utf8_bin NOT NULL,
  `version` varchar(100) COLLATE utf8_bin NOT NULL,
  `latest_active_time` datetime(6) DEFAULT NULL,
  `create_time` datetime(6) NOT NULL,
  `status` int(1) NOT NULL COMMENT '0:online  1:offline',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
