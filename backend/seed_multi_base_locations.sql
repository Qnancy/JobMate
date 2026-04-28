-- 为部分岗位写入多 base 地（顿号分隔），用于前端列表「等 n 城」与详情多标签展示。
-- 可按公司名 + 岗位名匹配，避免依赖 job id。
-- 可重复执行。

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = 'Java 后端开发工程师'
SET j.location = '杭州、北京、上海';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = '前端开发工程师 (React)'
SET j.location = '杭州、深圳、广州';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = '算法工程师 - 推荐方向'
SET j.location = '北京、上海、深圳、杭州、广州';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = '数据分析实习生'
SET j.location = '杭州、北京';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '腾讯' AND j.position = '后端开发工程师'
SET j.location = '深圳、广州、成都';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '腾讯' AND j.position = '游戏客户端开发工程师'
SET j.location = '上海、深圳、北京';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '腾讯' AND j.position = '安卓客户端开发实习生'
SET j.location = '深圳、北京';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '字节跳动' AND j.position = 'AI 算法工程师 - 大模型方向'
SET j.location = '北京、上海、深圳、杭州、新加坡';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '字节跳动' AND j.position = '推荐算法工程师'
SET j.location = '北京、上海';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '字节跳动' AND j.position = '后端开发实习生'
SET j.location = '杭州、北京、深圳';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '华为' AND j.position = '算法工程师'
SET j.location = '上海、北京、深圳';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '华为' AND j.position = '硬件工程师'
SET j.location = '深圳、东莞、西安';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '华为' AND j.position = '嵌入式软件开发工程师'
SET j.location = '南京、杭州、深圳';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '国家电网' AND j.position = '配电网规划工程师'
SET j.location = '杭州、南京、苏州、合肥';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = 'Microsoft' AND j.position = 'Software Engineer - Cloud'
SET j.location = '上海、北京、苏州';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '拼多多' AND j.position = '电商搜索算法工程师'
SET j.location = '上海、杭州、北京';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = 'OPPO' AND j.position = '影像算法工程师'
SET j.location = '深圳、成都、西安';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '美团' AND j.position = '外卖搜索算法工程师'
SET j.location = '北京、上海、深圳';

UPDATE job_infos j
INNER JOIN companies c ON c.id = j.company_id AND c.name = '蚂蚁集团' AND j.position = '风控策略分析师'
SET j.location = '杭州、上海、北京、深圳';
