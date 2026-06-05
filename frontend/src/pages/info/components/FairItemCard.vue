<template>
  <div class="bg-white rounded-xl overflow-hidden shadow-md border border-sky-50 hover:shadow-lg transition">
    <div class="h-2 bg-white"></div>
    <div class="p-4">
      <div class="flex justify-between items-start">
        <div class="flex-1" @click="emit('view', fair)">
          <div class="flex items-center gap-2">
            <span
              :class="[
                'px-2 py-0.5 text-xs font-medium rounded',
                fair.status === '报名中'
                  ? 'bg-green-100 text-green-600'
                  : fair.status === '即将开始'
                    ? 'bg-orange-100 text-orange-600'
                    : 'bg-gray-100 text-gray-600',
              ]"
            >
              {{ fair.status }}
            </span>
            <span class="text-xs text-gray-400">{{ fair.type }}</span>
          </div>
          <h3 class="font-bold text-gray-800 text-lg mt-2">{{ fair.title }}</h3>
          <div class="mt-3 space-y-1.5 text-sm text-gray-600">
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4 text-sky-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
              </svg>
              {{ fair.date }}
            </div>
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4 text-sky-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
              {{ fair.location }}
            </div>
            <div class="flex items-center gap-2">
              <svg class="w-4 h-4 text-sky-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4" />
              </svg>
              参展企业 {{ fair.companies }} 家
            </div>
          </div>
        </div>
        <button
          @click.stop="emit('toggle-favorite', fair.id)"
          class="p-2"
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
        <span></span>
        <button
          @click.stop="emit('view', fair)"
          :class="DETAIL_VIEW_CTA_CLASS"
        >
          查看详情 →
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { DETAIL_VIEW_CTA_CLASS } from '@/constants/detailViewCta';
import type { FairCard } from './cards';

defineProps<{
  fair: FairCard;
  favorited: boolean;
  animating: boolean;
}>();

const emit = defineEmits<{
  (e: 'view', fair: FairCard): void;
  (e: 'toggle-favorite', id: number): void;
}>();
</script>
