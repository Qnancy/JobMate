-- 创建数据库
CREATE DATABASE IF NOT EXISTS jobmate_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 创建用户并授权
CREATE USER IF NOT EXISTS 'jobmate_dev'@'%' IDENTIFIED BY 'JobMate@123';
GRANT ALL PRIVILEGES ON jobmate_db.* TO 'jobmate_dev'@'%';
FLUSH PRIVILEGES;