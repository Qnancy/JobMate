<template>
  <van-nav-bar title="管理员后台" left-arrow @click-left="goHome" />

  <div class="min-h-screen bg-w pt-3 px-3 pb-8">
    <template v-if="user">
      <div
        class="relative mt-2 mx-auto max-w-[600px] rounded-[18px] px-[18px] pt-6 pb-8 text-center text-white overflow-hidden shadow-[0_22px_40px_rgba(16,59,133,0.25)] bg-[linear-gradient(135deg,#214b97_0%,#3a7adf_60%,#6cb1ff_100%)]"
      >
        <div
          class="pointer-events-none absolute right-[-70px] top-[-50px] w-[220px] h-[220px] bg-[radial-gradient(circle,rgba(255,255,255,0.28)_0%,rgba(255,255,255,0)_70%)]"
        />
        <van-image
          class="border-2 border-white/55 shadow-[0_16px_36px_rgba(0,36,110,0.35)]"
          round
          width="80px"
          height="80px"
          fit="cover"
          :src="defaultAvatar"
        />
        <div class="mt-3 text-[18px] font-bold tracking-[0.3px]">{{ user.username }}</div>
        <div class="mt-1 text-[13px] opacity-90">系统管理员</div>
      </div>

      <div class="mt-4 mx-auto max-w-[640px] bg-white rounded-2xl overflow-hidden">
        <van-cell-group inset>
          <van-cell is-link :to="{ path: '/admin/jobs' }">
            <template #title>
              <div class="flex items-center gap-2 text-left">
                <van-icon name="todo-list-o" class="text-[22px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[15px] font-semibold">职位管理</span>
              </div>
            </template>
            <template #label>
              <span class="text-xs text-gray-400">独立页面：列表、发布、编辑、删除</span>
            </template>
          </van-cell>
          <van-cell is-link :to="{ path: '/admin/activities' }">
            <template #title>
              <div class="flex items-center gap-2 text-left">
                <van-icon name="bullhorn-o" class="text-[22px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[15px] font-semibold">宣讲会管理</span>
              </div>
            </template>
            <template #label>
              <span class="text-xs text-gray-400">独立页面：列表、发布、编辑、删除</span>
            </template>
          </van-cell>
          <van-cell is-link @click="showUserMgr = true">
            <template #title>
              <div class="flex items-center gap-2 text-left">
                <van-icon name="friends-o" class="text-[22px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[15px] font-semibold">用户信息</span>
              </div>
            </template>
            <template #label>
              <span class="text-xs text-gray-400">当前 API 仅支持查看当前登录用户</span>
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <div class="mt-4 mx-auto max-w-[640px]">
        <van-cell
          class="bg-white text-[#e45b5b] mx-2 my-3 rounded-[14px] shadow-[0_10px_24px_rgba(0,0,0,0.05)] border border-[#f1c7c7] transition duration-200 cursor-pointer hover:bg-[#fff4f4] hover:text-[#d63b3b]"
          center
          clickable
          @click="onLogout"
        >
          <template #title>
            <div class="text-[15px] font-bold text-center text-[#e45b5b]">退出登录</div>
          </template>
        </van-cell>
      </div>

      <van-popup v-model:show="showUserMgr" position="bottom" :style="{ height: '80%' }" round>
        <div class="p-4 h-full flex flex-col">
          <h3 class="text-lg font-bold mb-4">用户信息</h3>
          <div class="flex-1 overflow-y-auto">
            <van-list>
              <van-cell v-for="u in userList" :key="u.id" :title="u.username" :label="u.role" />
            </van-list>
            <div v-if="userList.length === 0" class="text-center text-gray-400 mt-10">暂无用户数据</div>
          </div>
          <van-button block type="primary" class="mt-4" @click="fetchUsers">刷新列表</van-button>
        </div>
      </van-popup>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from "vue";
import { useRouter } from "vue-router";
import { showToast, showSuccessToast } from "vant";
import * as auth from "@/services/auth";
import * as userService from "@/services/user";
import { isSuccessResponse } from "@/utils/request";

const router = useRouter();
const defaultAvatar = "/Zhejiang_University_Logo.svg.png";

const user = ref<auth.User | null>(null);
const showUserMgr = ref(false);
const userList = ref<userService.User[]>([]);

function goHome() {
  router.push("/");
}

watch(showUserMgr, (val) => {
  if (val) fetchUsers();
});

async function fetchUsers() {
  try {
    const res = await userService.getUsers();
    if (isSuccessResponse(res)) {
      userList.value = res.data || [];
      return;
    }
    showToast(res.message || "获取用户信息失败");
  } catch (e) {
    console.error(e);
  }
}

function onLogout() {
  auth.logout();
  user.value = null;
  showSuccessToast("已退出");
  router.push({ path: "/my/login-admin" });
}

onMounted(() => {
  const currentUser = auth.currentUser();
  if (!currentUser) {
    showToast("请先登录");
    router.push({ path: "/my/login-admin", query: { redirect: router.currentRoute.value.fullPath } });
    return;
  }
  if (currentUser.role !== "ADMIN") {
    showToast("无权访问管理员后台");
    router.push({ path: "/" });
    return;
  }
  user.value = currentUser;
});
</script>
