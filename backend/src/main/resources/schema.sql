-- ============================================
-- TCM Platform Database Schema (MySQL 8.0)
-- 任务一：执行此脚本初始化数据库
-- ============================================

CREATE DATABASE IF NOT EXISTS tcm_platform
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE tcm_platform;

-- 用户表 (医生/管理员)
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希(BCrypt)',
    role ENUM('admin', 'doctor') NOT NULL DEFAULT 'doctor' COMMENT '角色',
    display_name VARCHAR(100) COMMENT '显示名称',
    department VARCHAR(100) COMMENT '科室',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 患者账号表
CREATE TABLE patient_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希(BCrypt)',
    display_name VARCHAR(100) COMMENT '昵称',
    phone VARCHAR(20) COMMENT '手机号',
    avatar_url VARCHAR(500) COMMENT '头像地址',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='患者账号表';

-- 问诊单表（核心业务表）
CREATE TABLE consultations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    patient_account_id BIGINT COMMENT '患者账号ID',
    patient_name VARCHAR(100) NOT NULL COMMENT '患者姓名',
    age INT COMMENT '年龄',
    gender ENUM('男', '女', '其他') COMMENT '性别',
    phone VARCHAR(20) COMMENT '手机号',
    symptoms TEXT NOT NULL COMMENT '症状描述',
    duration VARCHAR(100) COMMENT '持续时间',
    allergy_history TEXT COMMENT '过敏史',
    urgency ENUM('普通', '紧急', '非常紧急') NOT NULL DEFAULT '普通' COMMENT '紧急度',
    patient_note TEXT COMMENT '患者备注',
    reminder_level ENUM('normal', 'attention', 'urgent') DEFAULT 'normal' COMMENT '提醒等级',
    reminder_text TEXT COMMENT '提醒文本',
    status ENUM('待接诊', '接诊中', '已完成') NOT NULL DEFAULT '待接诊' COMMENT '状态',
    doctor_note TEXT COMMENT '医生备注',
    doctor_id BIGINT COMMENT '处理医生ID',
    follow_up_at DATETIME COMMENT '跟进时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_patient_account (patient_account_id),
    INDEX idx_status (status),
    INDEX idx_urgency (urgency),
    INDEX idx_doctor (doctor_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (patient_account_id) REFERENCES patient_accounts(id) ON DELETE SET NULL,
    FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='问诊单表';

-- 中医常识文章表
CREATE TABLE knowledge_articles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    title VARCHAR(200) NOT NULL COMMENT '标题',
    category VARCHAR(50) COMMENT '分类',
    summary VARCHAR(500) COMMENT '摘要',
    content TEXT NOT NULL COMMENT '正文内容',
    tips JSON COMMENT '小贴士',
    cover_image_url VARCHAR(500) COMMENT '封面图片URL',
    published TINYINT(1) DEFAULT 0 COMMENT '是否发布(0=草稿,1=发布)',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category (category),
    INDEX idx_published (published)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='中医常识文章表';

-- 药膳表
CREATE TABLE recipes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '名称',
    season ENUM('春', '夏', '秋', '冬', '四季') COMMENT '适用季节',
    constitution VARCHAR(50) COMMENT '适用体质',
    suitable_for VARCHAR(200) COMMENT '适宜人群',
    summary VARCHAR(500) COMMENT '简介',
    ingredients JSON COMMENT '食材清单',
    steps JSON COMMENT '制作步骤',
    image_url VARCHAR(500) COMMENT '配图URL',
    published TINYINT(1) DEFAULT 0 COMMENT '是否发布(0=草稿,1=发布)',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_season (season),
    INDEX idx_constitution (constitution),
    INDEX idx_published (published)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='药膳表';

-- 上传记录表
CREATE TABLE uploads (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键ID',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    stored_name VARCHAR(255) NOT NULL COMMENT '存储文件名',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    file_size BIGINT COMMENT '文件大小(字节)',
    access_url VARCHAR(500) NOT NULL COMMENT '访问URL',
    uploaded_by BIGINT COMMENT '上传者ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    INDEX idx_stored_name (stored_name),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='上传记录表';

-- 初始化默认管理员账号 (密码: admin123, BCrypt加密)
INSERT INTO users (username, password_hash, role, display_name) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRcpMy.g9CsMA1JX02.1YDH4j/QiKMH8eZTjqS', 'admin', '系统管理员');

-- 初始化默认医生账号 (密码: doctor123, BCrypt加密)
INSERT INTO users (username, password_hash, role, display_name, department) VALUES
('doctor1', '$2a$10$N9qo8uLOickgx2ZMRcpMy.g9CsMA1JX02.1YDH4j/QiKMH8eZTjqS', 'doctor', '张医生', '内科');
