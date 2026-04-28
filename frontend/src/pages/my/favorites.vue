<template>
  <van-nav-bar
    title="我的收藏"
    left-text="返回"
    left-arrow
    @click-left="$router.back()"
  />

  <div class="min-h-screen bg-gradient-to-b from-sky-50 to-white">
    <van-tabs v-model:active="activeTab">
      <van-tab title="职位" name="JOB">
        <main class="p-4">
          <van-empty v-if="!loading && jobs.length === 0" description="还没有收藏的职位" />

          <div v-else class="space-y-3">
            <div
              v-for="item in jobs"
              :key="`job-${item.id}`"
              class="bg-white rounded-xl p-4 shadow-md border border-sky-50 hover:shadow-lg transition cursor-pointer"
              @click="viewJobDetail(item.target_id)"
            >
              <div class="flex justify-between items-start">
                <div class="flex-1 min-w-0">
                  <h3 class="font-bold text-gray-800 text-lg truncate">
                    {{ item.job?.position || '未知职位' }}
                  </h3>
                  <p class="text-sky-600 font-medium mt-1 truncate">
                    {{ item.job?.company?.name || '未知企业' }}
                  </p>
                  <div class="flex flex-wrap gap-2 mt-2">
                    <JobLocationChips :location="item.job?.location || '地点待定'" :max-tags="2" />
                    <span class="px-2 py-0.5 bg-blue-50 text-blue-600 text-xs rounded">
                      {{ mapRecruitType(item.job?.recruit_type) }}
                    </span>
                  </div>
                  <p class="text-xs text-gray-400 mt-2">
                    收藏于 {{ formatTime(item.created_at) }}
                  </p>
                </div>

                <button
                  class="p-2 ml-2 shrink-0"
                  aria-label="取消收藏"
                  @click.stop="onToggleFavorite('JOB', item.target_id)"
                >
                  <svg class="w-6 h-6 text-yellow-500 fill-yellow-500" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                      d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                  </svg>
                </button>
              </div>
            </div>
          </div>

          <van-loading v-if="loading" class="text-center mt-4" />
        </main>
      </van-tab>

      <van-tab title="活动" name="ACTIVITY">
        <main class="p-4">
          <van-empty v-if="!loading && activities.length === 0" description="还没有收藏的活动" />

          <div v-else class="space-y-3">
            <div
              v-for="item in activities"
              :key="`activity-${item.id}`"
              class="bg-white rounded-xl overflow-hidden shadow-md border border-sky-50 hover:shadow-lg transition"
            >
              <div class="p-4 cursor-pointer" @click="viewActivityDetail(item.target_id)">
                <div class="flex justify-between items-start">
                  <div class="flex-1 min-w-0">
                    <div class="flex items-center gap-2">
                      <span
                        :class="[
                          'px-2 py-0.5 text-xs font-medium rounded',
                          getActivityStatus(item.activity?.time) === '即将开始'
                            ? 'bg-orange-100 text-orange-600'
                            : 'bg-gray-100 text-gray-600'
                        ]"
                      >
                        {{ getActivityStatus(item.activity?.time) }}
                      </span>
                      <span class="text-xs text-gray-400">宣讲会</span>
                    </div>
                    <h3 class="font-bold text-gray-800 text-lg mt-2 truncate">
                      {{ item.activity?.title || '未知活动' }}
                    </h3>
                    <div class="mt-2 text-sm text-gray-600 space-y-1">
                      <div>{{ formatDateTime(item.activity?.time) }}</div>
                      <div>{{ item.activity?.location || '地点待定' }}</div>
                      <div>{{ item.activity?.company?.name || '未知企业' }}</div>
                    </div>
                    <p class="text-xs text-gray-400 mt-2">
                      收藏于 {{ formatTime(item.created_at) }}
                    </p>
                  </div>

                  <button
                    class="p-2 ml-2 shrink-0"
                    aria-label="取消收藏"
                    @click.stop="onToggleFavorite('ACTIVITY', item.target_id)"
                  >
                    <svg class="w-6 h-6 text-yellow-500 fill-yellow-500" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                        d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          </div>

          <van-loading v-if="loading" class="text-center mt-4" />
        </main>
      </van-tab>
    </van-tabs>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { withReturnFrom } from '@/utils/returnNavigation';
import { showToast } from 'vant';

import JobLocationChips from '@/components/JobLocationChips.vue';
import { listFavorites, type FavoriteItem, type FavoriteTargetType } from '@/services/favorite';
import { favoriteStore } from '@/utils/favoriteStore';
import { isSuccessResponse } from '@/utils/request';

const router = useRouter();
const route = useRoute();

const activeTab = ref<FavoriteTargetType>('JOB');
const jobs = ref<FavoriteItem[]>([]);
const activities = ref<FavoriteItem[]>([]);
const loading = ref(false);

/**
 * 收藏夹分页：一次拉 50 条，足够大多数用户当前需要；
 * 后续可换 van-list 做无限滚动，这里先一次全量拉完。
 */
async function fetchAll(type: FavoriteTargetType): Promise<FavoriteItem[]> {
  const pageSize = 50;
  let page = 1;
  const all: FavoriteItem[] = [];

  const maxPages = 500;
  while (page <= maxPages) {
    const res = await listFavorites({ target_type: type, page, page_size: pageSize });
    if (!isSuccessResponse(res) || !res.data) break;
    all.push(...(res.data.content || []));
    if (page >= (res.data.total_pages || 0) || (res.data.content || []).length === 0) break;
    page += 1;
  }
  return all;
}

async function loadAll() {
  loading.value = true;
  try {
    const [j, a] = await Promise.all([fetchAll('JOB'), fetchAll('ACTIVITY')]);
    jobs.value = j;
    activities.value = a;
    // 顺便把 store 的本地缓存刷新成最新的
    await favoriteStore.loadFromServer();
  } catch {
    showToast('加载收藏列表失败');
  } finally {
    loading.value = false;
  }
}

async function onToggleFavorite(type: FavoriteTargetType, targetId: number) {
  // 收藏夹页面只会"取消收藏"；乐观从列表移除，失败再恢复
  const backupJobs = jobs.value;
  const backupActivities = activities.value;
  if (type === 'JOB') {
    jobs.value = jobs.value.filter((item) => item.target_id !== targetId);
  } else {
    activities.value = activities.value.filter((item) => item.target_id !== targetId);
  }

  const nowFavorited = await favoriteStore.toggle(type, targetId);
  if (nowFavorited) {
    // 后端侧仍然是"已收藏"，说明 toggle 失败回滚了 store；也把 UI 恢复
    jobs.value = backupJobs;
    activities.value = backupActivities;
    showToast('取消收藏失败，请稍后再试');
  } else {
    showToast('已取消收藏');
  }
}

function viewJobDetail(id: number) {
  router.push(withReturnFrom(`/info/job/${id}`, route));
}

function viewActivityDetail(id: number) {
  router.push(withReturnFrom(`/info/activity/${id}`, route));
}

function mapRecruitType(type?: string | null) {
  const map: Record<string, string> = {
    INTERN: '实习',
    CAMPUS: '校招',
    EXPERIENCED: '校招',
  };
  if (!type) return '未知类型';
  return map[type] || type;
}

function getActivityStatus(time?: string | null) {
  if (!time) return '状态未知';
  const date = new Date(time.replace(' ', 'T'));
  if (Number.isNaN(date.getTime())) return '状态未知';
  return date.getTime() > Date.now() ? '即将开始' : '已结束';
}

function formatDateTime(time?: string | null) {
  if (!time) return '时间待定';
  return time.replace('T', ' ').replace(/:\d{2}(\.\d+)?(Z|[+-]\d{2}:?\d{2})?$/, '');
}

function formatTime(time?: string | null) {
  if (!time) return '';
  const date = new Date(time);
  if (Number.isNaN(date.getTime())) return time;
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}`;
}

onMounted(loadAll);
</script>
