<template>
<van-nav-bar
  title="搜索结果"
  left-text="Back"
  left-arrow
  @click-left="onClickLeft"
/>
<p class="result-hint">搜索 <span style="font-weight: bold;">{{ searchValue }}</span> 共 {{ searchResults.length }} 个结果</p>
<common-list :jobList="searchResults" />
</template>
<script setup>
import commonList from './components/list.vue';

import { ref } from 'vue';
import { useRoute } from 'vue-router';
const route = useRoute();

const searchValue = route.query.keywords || '';
const searchResults = ref([]);


const onClickLeft = () => {
  history.back()
}
</script>
<style scoped>

.result-hint {
  padding: 1em;
  font-size: 0.8em;
}
</style>