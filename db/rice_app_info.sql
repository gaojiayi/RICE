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

 Date: 11/02/2022 11:15:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for rice_app_info
-- ----------------------------
DROP TABLE IF EXISTS `rice_app_info`;
CREATE TABLE `rice_app_info` (
  `id` bigint(20) unsigned zerofill NOT NULL AUTO_INCREMENT,
  `app_name` varchar(20) COLLATE utf8_bin NOT NULL,
  `app_desc` varchar(255) COLLATE utf8_bin NOT NULL,
  `create_time` datetime(6) NOT NULL,
  `status` int(1) NOT NULL COMMENT '0 上线  1 下线',
  PRIMARY KEY (`id`),
  UNIQUE KEY `app_name_unique_key` (`app_name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

SET FOREIGN_KEY_CHECKS = 1;
