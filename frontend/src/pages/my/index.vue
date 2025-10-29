<template>
  <van-nav-bar title="我的" left-text=" " />

  <div class="page">
    <template v-if="user">

      <div style="text-align: center; margin-bottom: 16px;">
        <van-avatar :src="user.avatarUrl" size="64px" />
        <div style="margin-top: 8px; font-weight: bold;">{{ user.displayName || user.username }}</div>
      </div>

      <van-grid clickable :column-num="3">
        <van-grid-item icon="home-o" text="收藏夹" to="/my/favorites" />
        <van-grid-item icon="search" text="搜索" to="/search"/>
		<van-grid-item icon="setting-o" text="设置" to="/my/settings" />
		<van-grid-item icon="info-o" text="尚未实现，仅供参考" />
      </van-grid>

      <!-- more actions -->

      <div class="actions">
        <van-cell type="danger" @click="onLogout">退出登录</van-cell>
      </div>
    </template>

  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { Toast } from "vant";
import * as auth from "@/services/auth";

const router = useRouter();

const user = ref(auth.currentUser());

if (!user.value) {
  router.push({ path: "/my/login" });
}

function onLogout() {
  auth.logout();
  user.value = null;
  Toast.success("已退出");
}
</script>

<style scoped>
.page {
  padding: 16px;
}
.actions {
  margin-top: 24px;
}
</style>
