<template>
  <van-list
    v-model:loading="loading"
    :finished="finished"
    finished-text="没有更多信息了"
    @load="onLoad"
  >
    <van-card
      v-for="item in props.jobList"
      num="2"
      :title="item.title"
      :desc="item.description"
      :price="item.price"
      :thumb="item.thumb"
    >
      <template #footer>
        <van-button size="mini">收藏夹</van-button>
      </template>
    </van-card>
  </van-list>
</template>
<script setup>
import { ref } from "vue";
const props = defineProps({
  jobList: {
    type: Array,
    required: true,
  },
});

const loading = ref(false);
const finished = ref(true);
function onLoad() {
  // 模拟加载数据
  setTimeout(() => {
    finished.value = true;
    loading.value = false;
  }, 1000);
}
</script>
