-- 更新学习记录表结构

-- 添加缺失的created_at列
-- MySQL不支持ADD COLUMN IF NOT EXISTS语法，使用条件判断
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'study_records' AND COLUMN_NAME = 'created_at');
SET @query := IF(@exist = 0, 'ALTER TABLE study_records ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP', 'SELECT 1');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 添加缺失的updated_at列
-- MySQL不支持ADD COLUMN IF NOT EXISTS语法，使用条件判断
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'study_records' AND COLUMN_NAME = 'updated_at');
SET @query := IF(@exist = 0, 'ALTER TABLE study_records ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP', 'SELECT 1');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 重命名last_study_time列为last_study_at（如果存在）
-- MySQL不支持CHANGE COLUMN IF EXISTS语法，使用条件判断
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'study_records' AND COLUMN_NAME = 'last_study_time');
SET @query := IF(@exist > 0, 'ALTER TABLE study_records CHANGE COLUMN last_study_time last_study_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP', 'SELECT 1');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 重命名study_count列为study_time（如果存在）
-- MySQL不支持CHANGE COLUMN IF EXISTS语法，使用条件判断
SET @exist := (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'study_records' AND COLUMN_NAME = 'study_count');
SET @query := IF(@exist > 0, 'ALTER TABLE study_records CHANGE COLUMN study_count study_time INT DEFAULT 0', 'SELECT 1');
PREPARE stmt FROM @query;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;