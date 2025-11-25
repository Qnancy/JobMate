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

  <van-tabs v-model:active="activeName" class="glass-tabs">
    <van-tab title="职位" name="job">
      <!--具体加载数据等后端-->
      <common-list :list="[]" />
    </van-tab>
    <van-tab title="活动" name="event">
      <common-list :list="[]" />
    </van-tab>
  </van-tabs>
</template>
<script setup>
import { ref } from "vue";

const searchValue = ref("");
const activeName = ref("job");
</script>

<style scoped>
.glass-tabs {
  margin: 12px auto 32px;
  padding: 0 20px;
}

.glass-tabs:deep(.van-tabs__wrap) {
  border-radius: 999px;
  padding: 6px;
  background: rgba(255, 255, 255, 0.4);
  border: 1px solid rgba(255, 255, 255, 0.35);
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.2);
  backdrop-filter: blur(22px);
  -webkit-backdrop-filter: blur(22px);
}

.glass-tabs:deep(.van-tabs__nav) {
  background: transparent;
}

.glass-tabs:deep(.van-tab) {
  border-radius: 999px;
  font-weight: 600;
  color: rgba(15, 23, 42, 0.7);
}

.glass-tabs:deep(.van-tab--active) {
  color: #0f172a;
}

.glass-tabs:deep(.van-tabs__line) {
  height: calc(100% - 12px);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.85);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.8), 0 12px 24px rgba(15, 23, 42, 0.25);
  bottom: 6px;
}
</style>
