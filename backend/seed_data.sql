-- 清掉早期乱码公司（id 1-5，无引用）
DELETE FROM companies WHERE id IN (1,2,3,4,5);

-- 补充公司（已有 6-11: 阿里/腾讯/华为/国家电网/字节跳动/OPPO）
INSERT INTO companies (name, type) VALUES
  ('美团', 'PRIVATE'),
  ('百度', 'PRIVATE'),
  ('京东', 'PRIVATE'),
  ('小米', 'PRIVATE'),
  ('网易', 'PRIVATE'),
  ('快手', 'PRIVATE'),
  ('滴滴', 'PRIVATE'),
  ('拼多多', 'PRIVATE'),
  ('Shopee', 'FOREIGN'),
  ('Microsoft', 'FOREIGN'),
  ('中国移动', 'STATE'),
  ('中国银行', 'STATE'),
  ('海康威视', 'PRIVATE'),
  ('蚂蚁集团', 'PRIVATE')
ON DUPLICATE KEY UPDATE type=VALUES(type);

-- 用变量保存 company id（避免硬编码）
SET @c_ali     = (SELECT id FROM companies WHERE name='阿里巴巴');
SET @c_tx      = (SELECT id FROM companies WHERE name='腾讯');
SET @c_hw      = (SELECT id FROM companies WHERE name='华为');
SET @c_sg      = (SELECT id FROM companies WHERE name='国家电网');
SET @c_byte    = (SELECT id FROM companies WHERE name='字节跳动');
SET @c_oppo    = (SELECT id FROM companies WHERE name='OPPO');
SET @c_mt      = (SELECT id FROM companies WHERE name='美团');
SET @c_baidu   = (SELECT id FROM companies WHERE name='百度');
SET @c_jd      = (SELECT id FROM companies WHERE name='京东');
SET @c_mi      = (SELECT id FROM companies WHERE name='小米');
SET @c_ne      = (SELECT id FROM companies WHERE name='网易');
SET @c_ks      = (SELECT id FROM companies WHERE name='快手');
SET @c_dd      = (SELECT id FROM companies WHERE name='滴滴');
SET @c_pdd     = (SELECT id FROM companies WHERE name='拼多多');
SET @c_shopee  = (SELECT id FROM companies WHERE name='Shopee');
SET @c_ms      = (SELECT id FROM companies WHERE name='Microsoft');
SET @c_cmcc    = (SELECT id FROM companies WHERE name='中国移动');
SET @c_boc     = (SELECT id FROM companies WHERE name='中国银行');
SET @c_hk      = (SELECT id FROM companies WHERE name='海康威视');
SET @c_ant     = (SELECT id FROM companies WHERE name='蚂蚁集团');

-- 批量插入岗位
INSERT INTO job_infos (position, location, recruit_type, company_id, link, extra, created_at, updated_at, deadline) VALUES
  ('Java 后端开发工程师', '杭州', 'CAMPUS', @c_ali,
   'https://talent.alibaba.com/off-campus/position-detail?positionId=1001',
   '负责淘宝交易链路系统建设；要求熟悉 Spring/MyBatis/MySQL/Redis；有分布式经验优先。',
   NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 14 DAY),

  ('前端开发工程师 (React)', '杭州', 'CAMPUS', @c_ali,
   'https://talent.alibaba.com/off-campus/position-detail?positionId=1002',
   '阿里国际站团队，React + TypeScript 技术栈，负责 B 类商家管理后台。',
   NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 20 DAY),

  ('算法工程师 - 推荐方向', '杭州', 'CAMPUS', @c_ali,
   'https://talent.alibaba.com/off-campus/position-detail?positionId=1003',
   '淘宝信息流推荐，要求扎实机器学习基础，熟悉深度学习/排序模型者优先。',
   NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NOW() + INTERVAL 30 DAY),

  ('数据分析实习生', '杭州', 'INTERN', @c_ali,
   NULL,
   '实习每周不少于 4 天，持续 3 个月以上；要求熟悉 SQL/Python/Excel。',
   NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NOW() + INTERVAL 7 DAY),

  ('后端开发工程师', '深圳', 'CAMPUS', @c_tx,
   'https://join.qq.com/post.html?pid=2001',
   '微信支付后台，负责清结算系统；C++/Go 任一精通。',
   NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 21 DAY),

  ('游戏客户端开发工程师', '上海', 'CAMPUS', @c_tx,
   'https://join.qq.com/post.html?pid=2002',
   '腾讯天美工作室；Unity / Unreal 项目经验加分；扎实 C++ 基础。',
   NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 28 DAY),

  ('安卓客户端开发实习生', '深圳', 'INTERN', @c_tx,
   NULL,
   '微信安卓团队；要求熟悉 Kotlin/Android Jetpack；可实习 6 个月以上。',
   NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY, NOW() + INTERVAL 10 DAY),

  ('硬件工程师', '深圳', 'CAMPUS', @c_hw,
   'https://career.huawei.com/reccampportal/portal5/campus-recruitment-detail.html?id=3001',
   '海思半导体；负责 SoC 芯片硬件设计；电子/微电子专业优先。',
   NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY, NOW() + INTERVAL 25 DAY),

  ('嵌入式软件开发工程师', '南京', 'CAMPUS', @c_hw,
   'https://career.huawei.com/reccampportal/portal5/campus-recruitment-detail.html?id=3002',
   '华为终端 BG；C/C++ 嵌入式开发；熟悉 RTOS/Linux 驱动。',
   NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 18 DAY),

  ('AI 算法工程师 - 大模型方向', '北京', 'CAMPUS', @c_byte,
   'https://jobs.bytedance.com/campus/position/4001',
   '豆包大模型团队；负责预训练 / 微调 / RLHF；PyTorch 必备。',
   NOW() - INTERVAL 0 DAY, NOW() - INTERVAL 0 DAY, NOW() + INTERVAL 30 DAY),

  ('推荐算法工程师', '北京', 'CAMPUS', @c_byte,
   'https://jobs.bytedance.com/campus/position/4002',
   '抖音推荐技术团队；深度推荐模型经验加分。',
   NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 22 DAY),

  ('后端开发实习生', '杭州', 'INTERN', @c_byte,
   NULL,
   'TikTok 电商后端；Go 语言；可实习 6 个月以上。',
   NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NOW() + INTERVAL 5 DAY),

  ('影像算法工程师', '深圳', 'CAMPUS', @c_oppo,
   'https://careers.oppo.com/campus/5001',
   'OPPO 研究院计算影像团队；CV/图像处理基础扎实；有 ISP/HDR 经验加分。',
   NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NOW() + INTERVAL 15 DAY),

  ('配电网规划工程师', '杭州', 'CAMPUS', @c_sg,
   NULL,
   '国网浙江电力；电力系统专业；有电网规划课题经历优先。',
   NOW() - INTERVAL 8 DAY, NOW() - INTERVAL 8 DAY, NOW() + INTERVAL 12 DAY),

  ('外卖搜索算法工程师', '北京', 'CAMPUS', @c_mt,
   'https://campus.meituan.com/position/6001',
   '美团到家搜索；NLP / 检索 / 排序方向均可。',
   NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 20 DAY),

  ('Java 后端开发工程师', '北京', 'CAMPUS', @c_baidu,
   'https://talent.baidu.com/jobs/post/7001',
   '百度搜索基础架构；分布式存储/检索引擎方向。',
   NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY, NOW() + INTERVAL 18 DAY),

  ('全栈开发工程师', '北京', 'CAMPUS', @c_jd,
   'https://campus.jd.com/jobs/8001',
   '京东零售用户增长团队；React + Node.js / Java 全栈。',
   NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 6 DAY, NOW() + INTERVAL 14 DAY),

  ('IoT 嵌入式工程师', '北京', 'CAMPUS', @c_mi,
   'https://hr.xiaomi.com/jobs/9001',
   '小爱同学硬件团队；C/C++/RTOS；有 BLE/WiFi 协议栈经验加分。',
   NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY, NOW() + INTERVAL 25 DAY),

  ('游戏服务端开发工程师', '杭州', 'CAMPUS', @c_ne,
   'https://campus.163.com/job/10001',
   '网易游戏雷火；C++ / Lua；高并发服务器架构。',
   NOW() - INTERVAL 3 DAY, NOW() - INTERVAL 3 DAY, NOW() + INTERVAL 16 DAY),

  ('客户端开发实习生 (iOS)', '北京', 'INTERN', @c_ks,
   NULL,
   '快手 iOS App 团队；Swift；有上架 App Store 经验加分。',
   NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 8 DAY),

  ('地图算法工程师', '北京', 'CAMPUS', @c_dd,
   'https://campus.didiglobal.com/post/11001',
   '滴滴地图；ETA 预估 / 路径规划方向；图算法基础。',
   NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 7 DAY, NOW() - INTERVAL 1 DAY),

  ('电商搜索算法工程师', '上海', 'CAMPUS', @c_pdd,
   'https://careers.pinduoduo.com/job/12001',
   '拼多多搜索算法；Learning to Rank；线上 A/B 实验经验。',
   NOW() - INTERVAL 0 DAY, NOW() - INTERVAL 0 DAY, NOW() + INTERVAL 30 DAY),

  ('Software Engineer - Cloud', '上海', 'CAMPUS', @c_ms,
   'https://careers.microsoft.com/students/us/en/job/12345',
   'Azure 团队；C# / Rust；分布式系统方向。',
   NOW() - INTERVAL 5 DAY, NOW() - INTERVAL 5 DAY, NOW() + INTERVAL 21 DAY),

  ('网络安全工程师', '杭州', 'CAMPUS', @c_hk,
   'https://hr.hikvision.com/job/13001',
   '海康威视基础平台；安全协议 / 漏洞扫描 / 渗透测试。',
   NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY, NOW() + INTERVAL 12 DAY),

  ('风控策略分析师', '杭州', 'CAMPUS', @c_ant,
   'https://talent.antgroup.com/off-campus/14001',
   '蚂蚁支付宝交易风控；统计 / 机器学习背景。',
   NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY, NOW() + INTERVAL 20 DAY);

-- 批量插入活动（type: LECTURE / JOB_FAIR / COMPANY_VISIT）
INSERT INTO activity_infos (title, location, time, type, company_id, link, extra) VALUES
  ('阿里巴巴 2026 春招宣讲会', '玉泉校区永谦小剧场', NOW() + INTERVAL 3 DAY, 'LECTURE', @c_ali,
   'https://talent.alibaba.com/event/zju-2026',
   '主讲嘉宾：淘天集团技术 VP；议程包含技术分享 + HR 答疑；提供笔试直通卡。'),

  ('腾讯校招技术分享会', '紫金港校区圆正会议中心 109', NOW() + INTERVAL 5 DAY, 'LECTURE', @c_tx,
   'https://join.qq.com/event/2026',
   '微信事业群 / IEG 等多 BG 联合招聘；现场投递可获优先笔试资格。'),

  ('华为 ICT 招聘宣讲', '玉泉校区邵科馆', NOW() + INTERVAL 2 DAY, 'LECTURE', @c_hw,
   'https://career.huawei.com/event/zju-ict',
   '海思 + 华为云联合宣讲；研发 / 硬件 / AI 方向均开放。'),

  ('字节跳动技术宣讲会', '紫金港校区蒙民伟楼 225', NOW() + INTERVAL 7 DAY, 'LECTURE', @c_byte,
   'https://jobs.bytedance.com/campus/event/zju',
   '抖音 / TikTok / 豆包大模型团队联合分享。'),

  ('2026 互联网企业双选会', '紫金港校区体艺馆', NOW() + INTERVAL 10 DAY, 'JOB_FAIR', @c_ali,
   'https://career.zju.edu.cn/event/2026-internet',
   '阿里 / 腾讯 / 字节 / 美团 / 京东等 30+ 企业现场摆摊收简历。'),

  ('国企双选会 - 央企专场', '紫金港校区体艺馆', NOW() + INTERVAL 14 DAY, 'JOB_FAIR', @c_sg,
   'https://career.zju.edu.cn/event/2026-state',
   '国家电网 / 中国移动 / 中国银行 / 中国电信等央企集中招聘。'),

  ('硬件 & 智能终端企业双选会', '玉泉校区体育馆', NOW() + INTERVAL 6 DAY, 'JOB_FAIR', @c_oppo,
   'https://career.zju.edu.cn/event/2026-hw',
   'OPPO / 小米 / 海康 / vivo 等终端类企业联合招聘。'),

  ('阿里巴巴西溪园区参观', '杭州西湖区文一西路 969 号', NOW() + INTERVAL 9 DAY, 'COMPANY_VISIT', @c_ali,
   'https://talent.alibaba.com/visit/xixi',
   '名企探访活动；包含园区参观 + 工程师圆桌 + 茶歇；限 30 人。'),

  ('字节跳动飞书办公区参观', '北京海淀区中航广场', NOW() + INTERVAL 12 DAY, 'COMPANY_VISIT', @c_byte,
   'https://jobs.bytedance.com/visit/feishu',
   '名企探访；可与字节大模型工程师面对面交流。'),

  ('美团北京总部探访日', '北京朝阳区望京东路', NOW() + INTERVAL 15 DAY, 'COMPANY_VISIT', @c_mt,
   'https://campus.meituan.com/visit/beijing',
   '名企探访；外卖 / 优选 / 到店等 BG 工程师分享 + 园区开放日。'),

  ('Microsoft 校招线上分享会', '腾讯会议线上', NOW() + INTERVAL 4 DAY, 'LECTURE', @c_ms,
   'https://careers.microsoft.com/event/zju-online',
   'Azure / Office / 必应等多个产品线工程师在线分享技术与文化。'),

  ('网易雷火游戏开发者沙龙', '紫金港校区曹光彪楼', NOW() + INTERVAL 8 DAY, 'LECTURE', @c_ne,
   'https://campus.163.com/event/leihuo-zju',
   '主题：从校园到 3A 游戏研发；雷火主美 / 主程同台分享。');

-- 多 base 地样例（与 seed_multi_base_locations.sql 内容一致；顿号分隔）
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = 'Java 后端开发工程师' SET j.location = '杭州、北京、上海';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = '前端开发工程师 (React)' SET j.location = '杭州、深圳、广州';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = '算法工程师 - 推荐方向' SET j.location = '北京、上海、深圳、杭州、广州';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '阿里巴巴' AND j.position = '数据分析实习生' SET j.location = '杭州、北京';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '腾讯' AND j.position = '后端开发工程师' SET j.location = '深圳、广州、成都';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '腾讯' AND j.position = '游戏客户端开发工程师' SET j.location = '上海、深圳、北京';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '腾讯' AND j.position = '安卓客户端开发实习生' SET j.location = '深圳、北京';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '字节跳动' AND j.position = 'AI 算法工程师 - 大模型方向' SET j.location = '北京、上海、深圳、杭州、新加坡';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '字节跳动' AND j.position = '推荐算法工程师' SET j.location = '北京、上海';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '字节跳动' AND j.position = '后端开发实习生' SET j.location = '杭州、北京、深圳';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '华为' AND j.position = '算法工程师' SET j.location = '上海、北京、深圳';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '华为' AND j.position = '硬件工程师' SET j.location = '深圳、东莞、西安';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '华为' AND j.position = '嵌入式软件开发工程师' SET j.location = '南京、杭州、深圳';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '国家电网' AND j.position = '配电网规划工程师' SET j.location = '杭州、南京、苏州、合肥';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = 'Microsoft' AND j.position = 'Software Engineer - Cloud' SET j.location = '上海、北京、苏州';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '拼多多' AND j.position = '电商搜索算法工程师' SET j.location = '上海、杭州、北京';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = 'OPPO' AND j.position = '影像算法工程师' SET j.location = '深圳、成都、西安';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '美团' AND j.position = '外卖搜索算法工程师' SET j.location = '北京、上海、深圳';
UPDATE job_infos j INNER JOIN companies c ON c.id = j.company_id AND c.name = '蚂蚁集团' AND j.position = '风控策略分析师' SET j.location = '杭州、上海、北京、深圳';

-- 学历要求样例（与 seed_job_education_samples.sql 一致）
SET @preparedStatement = (SELECT IF(
  (SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS
   WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'job_infos' AND COLUMN_NAME = 'education_requirement') > 0,
  'SELECT 1',
  'ALTER TABLE job_infos ADD COLUMN education_requirement VARCHAR(32) NOT NULL DEFAULT ''UNSPECIFIED'''
));
PREPARE alterEduCol FROM @preparedStatement;
EXECUTE alterEduCol;
DEALLOCATE PREPARE alterEduCol;

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
