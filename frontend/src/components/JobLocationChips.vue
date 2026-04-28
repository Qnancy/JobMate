<template>
  <template v-for="(t, i) in summary.tags" :key="'loc-' + i">
    <span :class="tagClass">{{ t }}</span>
  </template>
  <span v-if="summary.moreCount > 0" :class="moreClass">等 {{ summary.moreCount }} 城</span>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { summarizeJobLocationsForList } from '@/utils/jobLocation';

const props = withDefaults(
  defineProps<{
    /** 后端单字段，多城用顿号等分隔 */
    location: string;
    /** 列表上最多展示几个城市标签 */
    maxTags?: number;
  }>(),
  { maxTags: 2 },
);

const summary = computed(() => summarizeJobLocationsForList(props.location, props.maxTags));

const tagClass = 'px-2 py-0.5 bg-sky-50 text-sky-600 text-xs rounded';

const moreClass = 'px-2 py-0.5 bg-gray-100 text-gray-600 text-xs rounded';
</script>
