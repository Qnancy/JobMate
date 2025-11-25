<template>
  <van-nav-bar title="我的" left-text=" " />

  <div class="page">
    <template v-if="user">

      <div class="profile-header">
        <van-image
          class="profile-avatar"
          round
          width="72px"
          height="72px"
          fit="cover"
          :src="user.avatarUrl || defaultAvatar"
        />
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
const defaultAvatar = 'https://avatars.githubusercontent.com/u/9919?s=200&v=4';

const user = ref(auth.currentUser());

if (!user.value) {
  router.push({ path: "/my/login" });
}

function onLogout() {
  auth.logout();
  user.value = null;
  Toast.success("已退出");
  router.push({ path: "/" });
}
</script>

<style scoped>
.page {
  padding: 16px;
}
.profile-header {
  text-align: center;
  margin-bottom: 16px;
}
.profile-avatar {
  border: 2px solid rgba(15, 23, 42, 0.15);
  box-shadow: 0 10px 25px rgba(15, 23, 42, 0.15);
}
.actions {
  margin-top: 24px;
}
</style>
