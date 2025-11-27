<template>
  <van-search
    v-model="searchValue"
    placeholder="搜索职位或活动"
    @search="$router.push({
      'path':'/info/search',
      'query':{
        'keywords': searchValue
      }
    })"
  />

  <van-tabs v-model:active="activeName" class="glass-tabs" >
    <van-tab title="职位" name="job">
      <!-- 使用更新后的 jobList 示例数据 -->
      <common-list :list="jobList" />
    </van-tab>
    <van-tab title="活动" name="event">
      <!-- 活动列表可以保持不变或按需修改 -->
      <common-list :list="eventList" />
    </van-tab>
  </van-tabs>
</template>

<script setup>
import { ref } from "vue";
import CommonList from "./components/list.vue";

const searchValue = ref("");
const activeName = ref("job");

// 职位列表的示例数据（已更新以匹配新样式）
const jobList = ref([
  {
    id: 1,
    title: '前端开发实习生',
    salary: '300-600元/天',
    company: '千帆叙梦',
    companySize: '20-99人',
    tags: ['本科', '4天/周', '3个月', 's架构', 'CSS样式'],
    hr: {
      avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg',
      name: '林女士',
      role: '负责人',
      activity: '回复率高'
    },
    location: {
      district: '余杭区',
      area: '仓前'
    }
  },
  {
    id: 2,
    title: 'Java后端工程师',
    salary: '25-45K·14薪',
    company: '字节跳动',
    companySize: '10000人以上',
    tags: ['本科', '5天/周', '6个月', 'Spring', 'MySQL'],
    hr: {
      avatar: 'https://fastly.jsdelivr.net/npm/@vant/assets/ipad.jpeg',
      name: '张先生',
      role: 'HR',
      activity: '今日活跃'
    },
    location: {
      district: '海淀区',
      area: '中关村'
    }
  }
]);

// 活动列表的示例数据
const eventList = ref([
  {
    id: 101,
    title: 'VueConf 2025全球开发者大会',
    subTitle: '上海世博中心 · 2025.12.15',
    tag: '技术分享',
    cover: 'https://fastly.jsdelivr.net/npm/@vant/assets/sandwiches.jpeg'
  }
]);
</script>

<style scoped>

</style>
