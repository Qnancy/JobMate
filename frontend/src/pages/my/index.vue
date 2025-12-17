<template>
  <van-nav-bar title="我的" left-text=" " />

  <div class="min-h-screen bg-w pt-3 px-3 pb-8">
    <template v-if="user">
      <div class="relative mt-2 mx-auto max-w-[600px] rounded-[18px] px-[18px] pt-6 pb-8 text-center text-white overflow-hidden shadow-[0_22px_40px_rgba(16,59,133,0.25)] bg-[linear-gradient(135deg,#38bdf8_0%,#3b82f6_100%)]">
        <div class="pointer-events-none absolute right-[-70px] top-[-50px] w-[220px] h-[220px] bg-[radial-gradient(circle,rgba(255,255,255,0.28)_0%,rgba(255,255,255,0)_70%)]"></div>
        <van-image
          class="border-2 border-white/55 shadow-[0_16px_36px_rgba(0,36,110,0.35)]"
          round
          width="80px"
          height="80px"
          fit="cover"
          :src="defaultAvatar"
        />
        <div class="mt-3 text-[18px] font-bold tracking-[0.3px]">{{ user.name }}</div>
        <div class="mt-1 text-[13px] opacity-90">浙江大学 · 计算机科学与技术</div>
      </div>

      <div class="mt-4 mx-auto max-w-[640px]">
        <van-cell-group :border="false" style="background: transparent">
          <van-cell
            class="mb-3 rounded-2xl bg-white text-[#1f2a44] text-[16px] font-semibold px-5 py-5 shadow-sm hover:bg-[#f5f8ff] hover:shadow-md hover:-translate-y-[1px] transition-all duration-200 items-center"
            :border="false"
            is-link
            to="/my/favorites"
          >
            <template #title>
              <div class="flex items-center gap-3 text-left">
                <van-icon name="home-o" class="text-[24px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[16px] font-semibold">收藏夹</span>
              </div>
            </template>
          </van-cell>
          <van-cell
            class="mb-3 rounded-2xl bg-white text-[#1f2a44] text-[16px] font-semibold px-5 py-5 shadow-sm hover:bg-[#f5f8ff] hover:shadow-md hover:-translate-y-[1px] transition-all duration-200 items-center"
            :border="false"
            is-link
            to="/my/settings"
          >
            <template #title>
              <div class="flex items-center gap-3 text-left">
                <van-icon name="setting-o" class="text-[24px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[16px] font-semibold">设置</span>
              </div>
            </template>
          </van-cell>
          <van-cell
            class="mb-3 rounded-2xl bg-white text-[#1f2a44] text-[16px] font-semibold px-5 py-5 shadow-sm hover:bg-[#f5f8ff] hover:shadow-md hover:-translate-y-[1px] transition-all duration-200 items-center"
            :border="false"
            is-link
          >
            <template #title>
              <div class="flex items-center gap-3 text-left">
                <van-icon name="info-o" class="text-[24px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[16px] font-semibold">尚未实现，仅供参考</span>
              </div>
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <div class="mt-4 mx-auto max-w-[640px]">
        <van-cell
          class="bg-white text-[#e45b5b] mx-2 my-3 rounded-[14px] shadow-[0_10px_24px_rgba(0,0,0,0.05)] border border-[#f1c7c7] transition duration-200 cursor-pointer hover:bg-[#fff4f4] hover:text-[#d63b3b] hover:shadow-[0_10px_24px_rgba(255,140,140,0.18)] hover:-translate-y-[2px]"
          center
          clickable
          @click="onLogout"
        >
          <template #title>
            <div class="text-[15px] font-bold text-center text-[#e45b5b]">退出登录</div>
          </template>
        </van-cell>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { showSuccessToast } from "vant";
import * as auth from "@/services/auth";

const router = useRouter();
const defaultAvatar = '/Zhejiang_University_Logo.svg.png';

const user = ref(auth.currentUser());

if (!user.value) {
  router.push({ path: "/my/login" });
}

function onLogout() {
  auth.logout();
  user.value = null;
  showSuccessToast("已退出");
  router.push({ path: "/" });
}
</script>
