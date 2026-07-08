-- ============================================
-- 校园时光胶囊管理系统 数据库初始化脚本
-- 数据库名: capsule_db
-- ============================================

CREATE DATABASE IF NOT EXISTS capsule_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE capsule_db;

-- ============================================
-- 1. 用户表
-- ============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名（登录账号）',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（MD5加密）',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色: USER-普通用户, ADMIN-管理员',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 分类表
-- ============================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
    `sort` INT DEFAULT 0 COMMENT '排序号',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='胶囊分类表';

-- ============================================
-- 3. 时光胶囊表
-- ============================================
DROP TABLE IF EXISTS `capsule`;
CREATE TABLE `capsule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '胶囊ID',
    `title` VARCHAR(100) NOT NULL COMMENT '胶囊标题',
    `summary` VARCHAR(300) DEFAULT NULL COMMENT '胶囊摘要',
    `content` TEXT NOT NULL COMMENT '胶囊正文内容',
    `cover` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
    `owner_id` BIGINT NOT NULL COMMENT '创建者用户ID',
    `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
    `visibility` VARCHAR(10) NOT NULL DEFAULT 'PRIVATE' COMMENT '可见性: PUBLIC-公开, PRIVATE-私密',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-正常, 0-已删除',
    `open_time` DATETIME DEFAULT NULL COMMENT '解封开启时间',
    `view_count` INT DEFAULT 0 COMMENT '浏览量',
    `like_count` INT DEFAULT 0 COMMENT '点赞数',
    `favorite_count` INT DEFAULT 0 COMMENT '收藏数',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_owner_id` (`owner_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_visibility` (`visibility`),
    KEY `idx_open_time` (`open_time`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时光胶囊表';

-- ============================================
-- 4. 评论表
-- ============================================
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `capsule_id` BIGINT NOT NULL COMMENT '所属胶囊ID',
    `user_id` BIGINT NOT NULL COMMENT '评论者用户ID',
    `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
    `parent_id` BIGINT DEFAULT NULL COMMENT '父评论ID（用于回复）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    PRIMARY KEY (`id`),
    KEY `idx_capsule_id` (`capsule_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评论表';

-- ============================================
-- 5. 收藏表
-- ============================================
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `capsule_id` BIGINT NOT NULL COMMENT '胶囊ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_capsule` (`user_id`, `capsule_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_capsule_id` (`capsule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ============================================
-- 6. 点赞记录表
-- ============================================
DROP TABLE IF EXISTS `like_record`;
CREATE TABLE `like_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `capsule_id` BIGINT NOT NULL COMMENT '胶囊ID',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_capsule` (`user_id`, `capsule_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_capsule_id` (`capsule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='点赞记录表';

-- ============================================
-- 7. 公告表
-- ============================================
DROP TABLE IF EXISTS `notice`;
CREATE TABLE `notice` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    `title` VARCHAR(100) NOT NULL COMMENT '公告标题',
    `content` TEXT NOT NULL COMMENT '公告内容',
    `publisher_id` BIGINT NOT NULL COMMENT '发布者ID',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 1-已发布, 0-草稿',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- ============================================
-- 初始化数据
-- ============================================

-- 初始化管理员账号（密码: admin123，MD5加密）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', '0192023a7bbd73250516f069df18b500', '系统管理员', 'ADMIN', 1);

-- 初始化分类数据
INSERT INTO `category` (`name`, `description`, `sort`) VALUES
('学业目标', '记录学习计划和学业目标', 1),
('生活感悟', '日常生活中的感悟和思考', 2),
('未来梦想', '对未来的期望和梦想', 3),
('友情记忆', '和朋友之间的珍贵回忆', 4),
('校园故事', '校园里发生的有趣故事', 5),
('毕业寄语', '毕业时想对自己说的话', 6);

-- 初始化一条示例公告
INSERT INTO `notice` (`title`, `content`, `publisher_id`, `status`) VALUES
('欢迎使用校园时光胶囊系统', '亲爱的同学们，欢迎使用校园时光胶囊管理系统！在这里你可以记录当下的目标、梦想和生活片段，在未来某一天重新开启，感受成长的变化。快来创建你的第一个时光胶囊吧！', 1, 1);
