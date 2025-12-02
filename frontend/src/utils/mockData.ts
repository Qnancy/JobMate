// 职位标签
const jobTags = ['全部', '技术', '产品', '设计', '运营', '市场']

export function fetchJobTags() {
  return new Promise<string[]>((resolve) => {
    setTimeout(() => {
        resolve(jobTags);
    }, 500);
  });
}

// Mock 职位数据
const jobs = [
  {
    id: 1,
    title: '前端开发工程师',
    company: '阿里巴巴',
    location: '杭州',
    type: '全职',
    salary: '25-40K',
    description: '负责集团核心业务前端开发，参与技术架构设计与优化',
    category: '技术',
    publishDate: '2024-01-15',
    companyType: '互联网',
    companySize: '10000人以上',
    fullDescription: `
      <p><strong>岗位职责：</strong></p>
      <ul class="list-disc pl-4 space-y-1 mt-2">
        <li>负责阿里巴巴核心业务的前端开发工作</li>
        <li>参与前端技术架构设计与性能优化</li>
        <li>与产品、设计、后端紧密配合，确保产品高质量交付</li>
        <li>参与前端工程化建设，提升团队研发效率</li>
      </ul>
    `,
    requirements: `
      <ul class="list-disc pl-4 space-y-1">
        <li>本科及以上学历，计算机相关专业优先</li>
        <li>精通 HTML、CSS、JavaScript，熟悉 ES6+ 语法</li>
        <li>熟练使用 Vue 或 React 框架</li>
        <li>良好的沟通能力和团队协作精神</li>
        <li>有大型项目经验者优先</li>
      </ul>
    `
  },
  {
    id: 2,
    title: '产品经理',
    company: '字节跳动',
    location: '北京',
    type: '全职',
    salary: '30-50K',
    description: '负责抖音相关产品的规划与设计，推动产品迭代优化',
    category: '产品',
    publishDate: '2024-01-14',
    companyType: '互联网',
    companySize: '10000人以上',
    fullDescription: `
      <p><strong>岗位职责：</strong></p>
      <ul class="list-disc pl-4 space-y-1 mt-2">
        <li>负责抖音产品功能的规划与设计</li>
        <li>深入分析用户需求，输出高质量的产品方案</li>
        <li>跟进产品开发进度，确保按时高质量上线</li>
        <li>持续关注数据指标，推动产品迭代优化</li>
      </ul>
    `,
    requirements: `
      <ul class="list-disc pl-4 space-y-1">
        <li>本科及以上学历，3年以上产品经验</li>
        <li>优秀的逻辑思维和数据分析能力</li>
        <li>出色的沟通协调能力</li>
        <li>对短视频/社交产品有深刻理解者优先</li>
      </ul>
    `
  },
  {
    id: 3,
    title: 'UI/UX设计师',
    company: '网易',
    location: '杭州',
    type: '全职',
    salary: '20-35K',
    description: '负责网易游戏相关产品的UI设计与用户体验优化',
    category: '设计',
    publishDate: '2024-01-13',
    companyType: '互联网',
    companySize: '10000人以上',
    fullDescription: `
      <p><strong>岗位职责：</strong></p>
      <ul class="list-disc pl-4 space-y-1 mt-2">
        <li>负责网易游戏产品的界面设计</li>
        <li>参与用户研究，优化产品用户体验</li>
        <li>制定设计规范，维护设计系统</li>
        <li>与开发团队紧密配合，确保设计落地</li>
      </ul>
    `,
    requirements: `
      <ul class="list-disc pl-4 space-y-1">
        <li>本科及以上学历，设计相关专业</li>
        <li>熟练使用 Figma、Sketch 等设计工具</li>
        <li>具备良好的审美和设计思维</li>
        <li>有游戏设计经验者优先</li>
      </ul>
    `
  },
  {
    id: 4,
    title: '后端开发工程师',
    company: '腾讯',
    location: '深圳',
    type: '全职',
    salary: '28-45K',
    description: '负责微信支付核心系统的研发与维护',
    category: '技术',
    publishDate: '2024-01-12',
    companyType: '互联网',
    companySize: '10000人以上',
    fullDescription: `
      <p><strong>岗位职责：</strong></p>
      <ul class="list-disc pl-4 space-y-1 mt-2">
        <li>负责微信支付核心系统的设计与开发</li>
        <li>保障系统的高可用性和高性能</li>
        <li>参与技术方案评审和代码审核</li>
        <li>持续优化系统架构，提升研发效率</li>
      </ul>
    `,
    requirements: `
      <ul class="list-disc pl-4 space-y-1">
        <li>本科及以上学历，计算机相关专业</li>
        <li>精通 Java/Go/C++ 其中一门语言</li>
        <li>熟悉分布式系统设计</li>
        <li>有高并发系统经验者优先</li>
      </ul>
    `
  }
]

// Mock 招聘会数据
const fairs = [
  {
    id: 1,
    title: '2024春季大型综合招聘会',
    date: '2024年3月15日 9:00-17:00',
    location: '浙江大学紫金港校区体育馆',
    type: '综合招聘会',
    status: '报名中',
    companies: 156,
    companyList: ['阿里巴巴', '腾讯', '字节跳动', '华为', '网易', '美团', '京东', '小米'],
    description: `
      <p>本次招聘会是浙江大学2024年规模最大的综合性招聘会，汇集<strong>156家知名企业</strong>，涵盖互联网、金融、制造业、咨询等多个行业。</p>
      <p class="mt-2">预计提供岗位<strong>2000+</strong>个，涉及技术、产品、运营、市场等多个方向。</p>
      <p class="mt-2 text-sky-600">欢迎2024届及往届毕业生积极参与！</p>
    `,
    notice: `
      <ul class="list-disc pl-4 space-y-2">
        <li>请携带<strong>学生证</strong>或<strong>校园卡</strong>入场</li>
        <li>建议提前准备好<strong>简历</strong>（纸质版和电子版）</li>
        <li>着装得体，展现专业形象</li>
        <li>可提前在就业信息平台查看参展企业及岗位信息</li>
        <li>招聘会当天提供<strong>免费午餐</strong></li>
      </ul>
    `
  },
  {
    id: 2,
    title: '互联网专场招聘会',
    date: '2024年3月20日 14:00-18:00',
    location: '浙江大学玉泉校区永谦活动中心',
    type: '专场招聘会',
    status: '即将开始',
    companies: 45,
    companyList: ['阿里巴巴', '字节跳动', '美团', '滴滴', '快手', '哔哩哔哩'],
    description: `
      <p>互联网行业专场招聘会，汇集<strong>45家头部互联网企业</strong>。</p>
      <p class="mt-2">主要招聘方向：</p>
      <ul class="list-disc pl-4 mt-1">
        <li>软件开发工程师</li>
        <li>算法工程师</li>
        <li>产品经理</li>
        <li>数据分析师</li>
      </ul>
    `,
    notice: `
      <ul class="list-disc pl-4 space-y-2">
        <li>仅面向<strong>计算机、软件、电子信息</strong>等相关专业学生</li>
        <li>需提前在线报名，现场凭<strong>报名二维码</strong>入场</li>
        <li>建议提前了解目标公司及岗位</li>
      </ul>
    `
  },
  {
    id: 3,
    title: '金融行业精英招聘会',
    date: '2024年3月25日 9:00-12:00',
    location: '浙江大学紫金港校区蒙民伟楼',
    type: '专场招聘会',
    status: '报名中',
    companies: 32,
    companyList: ['中金公司', '高盛', '摩根士丹利', '招商银行', '蚂蚁集团', '中信证券'],
    description: `
      <p>金融行业专场招聘，<strong>32家顶级金融机构</strong>参展。</p>
      <p class="mt-2">岗位类型包括：投资银行、资产管理、风险控制、金融科技等。</p>
    `,
    notice: `
      <ul class="list-disc pl-4 space-y-2">
        <li>主要面向<strong>金融、经济、数学、统计</strong>等专业学生</li>
        <li>请携带中英文简历各5份</li>
        <li>部分企业有现场笔试，请做好准备</li>
      </ul>
    `
  }
]


// mock fetchJob, fetchFair, fetchJobDetail, fetchFairDetail functions
export function fetchJobs() {
  return new Promise((resolve) => { 
    setTimeout(() => {
      resolve(jobs);
    }, 500);
  }
    );  
}

export function fetchFairs() {
  return new Promise((resolve) => { 
    setTimeout(() => {
      resolve(fairs);
    }, 500);
  }
    );  
}
export function fetchJobDetail(id: number) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const job = jobs.find((job) => job.id == id);
      console.log(job,id);
      
      resolve(job);
    }, 500);
  });
}
export function fetchFairDetail(id: number) {
  return new Promise((resolve) => {
    setTimeout(() => {
      const fair = fairs.find((fair) => fair.id === id);
      resolve(fair);
    }, 500);
  });
}

// generate similar mocks about fairs

// mock favourites for fairs
const favouriteFairIds: number[] = [];

export function fetchFavouriteFairIds(): Promise<number[]> {
    return new Promise((resolve) => {
        setTimeout(() => {
            resolve(favouriteFairIds);
        }, 300);
    });
}

export function addFavouriteFairId(id: number): Promise<void> {
    return new Promise((resolve) => {
        setTimeout(() => {
            if (!favouriteFairIds.includes(id)) {
                favouriteFairIds.push(id);
            }
            resolve();
        }, 300);
    });
}

export function removeFavouriteFairId(id: number): Promise<void> {
    return new Promise((resolve) => {
        setTimeout(() => {
            const index = favouriteFairIds.indexOf(id);
            if (index !== -1) {
                favouriteFairIds.splice(index, 1);
            }
            resolve();
        }, 300);
    });
}

export function fetchIsFairFavorite(id: number): boolean {
    return favouriteFairIds.includes(id);
}

// utilities to query fairs
export function fetchFairsByType(type: string) {
    return new Promise<typeof fairs>((resolve) => {
        setTimeout(() => {
            resolve(fairs.filter((f) => f.type === type));
        }, 500);
    });
}

export function fetchFairsByStatus(status: string) {
    return new Promise<typeof fairs>((resolve) => {
        setTimeout(() => {
            resolve(fairs.filter((f) => f.status === status));
        }, 500);
    });
}


// mock a list of favourite job IDs
const favouriteJobIds: number[] = [];
export function fetchFavouriteJobIds() {
  return new Promise<number[]>((resolve) => {
    setTimeout(() => {
      resolve(favouriteJobIds);
    }, 300);
  });   
}
export function addFavouriteJobId(id: number) {
  return new Promise<void>((resolve) => {
    setTimeout(() => {
      if (!favouriteJobIds.includes(id)) {
        favouriteJobIds.push(id);
      }
      resolve();
    }, 300);
  });
}
export function removeFavouriteJobId(id: number) {
  return new Promise<void>((resolve) => {
    setTimeout(() => {
      const index = favouriteJobIds.indexOf(id);
      if (index !== -1) {
        favouriteJobIds.splice(index, 1);
      }
      resolve();
    }, 300);
  });
}
export function fetchIsJobFavorite(id: number) {
  return favouriteJobIds.includes(id);
}
