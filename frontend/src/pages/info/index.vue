<template>
  <van-search v-model="searchValue" placeholder="Placeholder" @search="$router.push({
    'path':'/info/search',
    'query':{
      'keywords': searchValue
    }
  })"/>

  <van-tabs v-model:active="activeName">
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
</script>
