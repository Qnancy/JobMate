-- 为职位写入多样化 education_requirement（与枚举名一致），按「公司 + 岗位」匹配，可重复执行。
-- 若库表尚无该列（尚未跑过带 Hibernate 的后端），先补列。

SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'job_infos' AND COLUMN_NAME = 'education_requirement') > 0,
  'SELECT 1',
  'ALTER TABLE job_infos ADD COLUMN education_requirement VARCHAR(32) NOT NULL DEFAULT ''UNSPECIFIED'''
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = 'Java 后端开发工程师' SET j.education_requirement = 'BACHELOR_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = '前端开发工程师 (React)' SET j.education_requirement = 'BACHELOR_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = '算法工程师 - 推荐方向' SET j.education_requirement = 'MASTER_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = '数据分析实习生' SET j.education_requirement = 'BACHELOR_AND_ABOVE';

UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '腾讯' AND j.position = '后端开发工程师' SET j.education_requirement = 'BACHELOR_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '腾讯' AND j.position = '游戏客户端开发工程师' SET j.education_requirement = 'BACHELOR_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '腾讯' AND j.position = '安卓客户端开发实习生' SET j.education_requirement = 'UNSPECIFIED';

UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '华为' AND j.position = '算法工程师' SET j.education_requirement = 'MASTER_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '华为' AND j.position = '硬件工程师' SET j.education_requirement = 'MASTER_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '华为' AND j.position = '嵌入式软件开发工程师' SET j.education_requirement = 'BACHELOR_AND_ABOVE';

UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '字节跳动' AND j.position = 'AI 算法工程师 - 大模型方向' SET j.education_requirement = 'PHD_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '字节跳动' AND j.position = '推荐算法工程师' SET j.education_requirement = 'MASTER_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '字节跳动' AND j.position = '后端开发实习生' SET j.education_requirement = 'BACHELOR_AND_ABOVE';

UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = 'OPPO' AND j.position = '影像算法工程师' SET j.education_requirement = 'MASTER_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '国家电网' AND j.position = '配电网规划工程师' SET j.education_requirement = 'BACHELOR_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '美团' AND j.position = '外卖搜索算法工程师' SET j.education_requirement = 'MASTER_AND_ABOVE';

UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '百度' AND j.position = 'Java 后端开发工程师' SET j.education_requirement = 'BACHELOR_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '京东' AND j.position = '全栈开发工程师' SET j.education_requirement = 'UNSPECIFIED';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '小米' AND j.position = 'IoT 嵌入式工程师' SET j.education_requirement = 'BACHELOR_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '网易' AND j.position = '游戏服务端开发工程师' SET j.education_requirement = 'BACHELOR_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '快手' AND j.position = '客户端开发实习生 (iOS)' SET j.education_requirement = 'BACHELOR_AND_ABOVE';

UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '滴滴' AND j.position = '地图算法工程师' SET j.education_requirement = 'MASTER_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '拼多多' AND j.position = '电商搜索算法工程师' SET j.education_requirement = 'MASTER_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = 'Microsoft' AND j.position = 'Software Engineer - Cloud' SET j.education_requirement = 'MASTER_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '海康威视' AND j.position = '网络安全工程师' SET j.education_requirement = 'BACHELOR_AND_ABOVE';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '蚂蚁集团' AND j.position = '风控策略分析师' SET j.education_requirement = 'PHD_AND_ABOVE';
