<template>
  <div
    class="bg-white rounded-xl p-4 shadow-md border border-sky-50 hover:shadow-lg transition cursor-pointer"
    @click="emit('view', job)"
  >
    <div class="flex justify-between items-start">
      <div class="flex-1 min-w-0">
        <h3 class="m-0 text-gray-800">
          <div class="flex items-start gap-2.5">
            <span
              class="shrink-0 mt-0.5 inline-flex items-center px-2 py-1 rounded-md text-[11px] font-semibold tracking-wide leading-none"
              :class="recruitBadgeClass(job.type)"
            >{{ job.type }}</span>
            <span class="min-w-0 flex-1 font-bold text-lg leading-snug">{{ job.title }}</span>
          </div>
        </h3>
        <p class="text-sky-600 font-medium mt-1">{{ job.company }}</p>
        <div class="flex flex-wrap gap-2 mt-2">
          <JobLocationChips :location="job.location" :max-tags="2" />
          <span class="px-2 py-0.5 bg-violet-50 text-violet-700 text-xs rounded">{{ job.educationLabel }}</span>
          <span class="px-2 py-0.5 bg-green-50 text-green-600 text-xs rounded">{{ job.salary }}</span>
        </div>
        <p class="text-gray-500 text-sm mt-2 line-clamp-2">{{ job.description }}</p>
      </div>
      <button
        @click.stop="emit('toggle-favorite', job.id)"
        class="p-2 ml-2"
      >
        <svg
          class="w-6 h-6 transition-transform duration-150"
          :class="[
            favorited ? 'text-yellow-500 fill-yellow-500' : 'text-gray-300',
            animating ? 'scale-125' : '',
          ]"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
        </svg>
      </button>
    </div>
    <div class="flex justify-between items-center mt-3 pt-3 border-t border-gray-100">
      <div class="flex items-center gap-2 text-xs text-gray-400">
        <span>{{ job.publishDate }}</span>
        <span
          v-if="job.deadlineText"
          :class="job.deadlineText === '已截止' ? 'text-gray-400' : 'text-orange-500'"
        >· {{ job.deadlineText }}</span>
      </div>
      <button
        @click.stop="emit('view', job)"
        class="text-sky-500 text-sm font-medium hover:text-sky-600"
      >
        查看详情 →
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import JobLocationChips from '@/components/JobLocationChips.vue';
import type { JobCard } from './cards';

defineProps<{
  job: JobCard;
  favorited: boolean;
  animating: boolean;
}>();

/** 校招 / 实习 等招聘类型，与 {@link cards.RECRUIT_TYPE_MAP} 展示文案一致 */
function recruitBadgeClass(type: string) {
  if (type === '实习') return 'bg-amber-100 text-amber-900 ring-1 ring-amber-200/80';
  if (type === '校招') return 'bg-sky-100 text-sky-900 ring-1 ring-sky-200/80';
  return 'bg-slate-100 text-slate-700 ring-1 ring-slate-200/80';
}

const emit = defineEmits<{
  (e: 'view', job: JobCard): void;
  (e: 'toggle-favorite', id: number): void;
}>();
</script>
