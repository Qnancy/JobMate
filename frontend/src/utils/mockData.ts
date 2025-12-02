// mockData.js

const mockJobs = [
  {
    id: 1,
    title: '前端开发工程师 (Web3方向)',
    company: '蚂蚁金服',
    location: '杭州/上海',
    salary: '25K-40K/月',
    isCollected: false,
    detailHtml: '<p><strong>岗位职责：</strong></p><ul><li>负责 Web3 相关应用的前端开发工作。</li><li>参与项目架构设计和性能优化。</li></ul><p><strong>任职要求：</strong></p><p>精通 Vue.js 或 React，熟悉 TypeScript。</p>'
  },
  {
    id: 2,
    title: '算法工程师 (CV方向)',
    company: '字节跳动',
    location: '北京/深圳',
    salary: '30K-55K/月',
    isCollected: true,
    detailHtml: '<p><strong>岗位职责：</strong></p><p>负责计算机视觉相关算法的研究、开发和落地。</p>'
  },
  {
    id: 3,
    title: '管培生 (金融科技)',
    company: '工商银行',
    location: '全国',
    salary: '面议',
    isCollected: false,
    detailHtml: '<p>面向应届毕业生，提供多岗位轮岗机会。</p>'
  }
];

const mockFairs = [
  {
    id: 101,
    title: '浙江大学2025届毕业生春季大型双选会',
    date: '2025-03-15',
    time: '09:00 - 16:00',
    venue: '紫金港校区体育馆',
    isCollected: false,
    detailHtml: '<p><strong>参会企业：</strong>预计300+家，涵盖互联网、金融、制造等行业。</p><p><strong>注意事项：</strong>请携带学生证和个人简历。</p>'
  },
  {
    id: 102,
    title: '阿里技术专场招聘会 (校园行)',
    date: '2025-04-01',
    time: '14:00 - 17:00',
    venue: '玉泉校区永谦活动中心',
    isCollected: true,
    detailHtml: '<p><strong>面向对象：</strong>计算机、软件工程等相关专业学生。</p>'
  }
];

export function fetchJobs() {
  return new Promise(resolve => {
    setTimeout(() => resolve(JSON.parse(JSON.stringify(mockJobs))), 200);
  });
}

export function fetchJobDetail(id) {
  return new Promise(resolve => {
    const job = mockJobs.find(j => j.id === id);
    setTimeout(() => resolve(job), 200);
  });
}

export function fetchFairs() {
  return new Promise(resolve => {
    setTimeout(() => resolve(JSON.parse(JSON.stringify(mockFairs))), 200);
  });
}

export function fetchFairDetail(id) {
  return new Promise(resolve => {
    const fair = mockFairs.find(f => f.id === id);
    setTimeout(() => resolve(fair), 200);
  });
}