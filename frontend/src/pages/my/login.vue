<template>
  <van-nav-bar title="登录" left-text=" " />

  <div class="page">
    <div class="logo">
      <img src="@/assets/vue.svg" alt="" />
      <p>(logo)</p>
    </div>

    <van-form @submit="onSubmit">
      <van-field
        v-model="form.username"
        label="用户名"
        placeholder="请输入用户名"
        clearable
        @update:value="saveForm"
      />
      <van-field
        v-model="form.password"
        :type="showPassword ? 'text' : 'password'"
        label="密码"
        placeholder="请输入密码"
        clearable
        @update:value="saveForm"
      >
        <template #right-icon>
          <div @click.stop="showPassword = !showPassword" style="cursor: pointer; padding: 0 8px; display: flex; align-items: center; user-select: none;">
            <van-icon :name="showPassword ? 'eye-o' : 'closed-eye'" />
          </div>
        </template>
      </van-field>

      <div style="margin-top: 1.5em; margin-left: 2em; margin-right: 2em">
        <van-button block round type="primary" native-type="submit"
          >登录</van-button
        >
      </div>
    </van-form>

    <div style="margin-top: 12px; text-align: center; font-size: 0.5em">
      <router-link to="/my/register">还没有账号？去注册</router-link>
    </div>
    <div style="margin-top: 24px; text-align: center">
      <van-button round size="small" type="success"
        >浙大统一身份认证</van-button
      >
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watchEffect } from "vue";
import { useRouter, onBeforeRouteLeave } from "vue-router";
import { Toast } from "vant";
import * as auth from "@/services/auth";

const router = useRouter();

const STORAGE_KEY = "jobmate_login_form";
const form = ref({ username: "", password: "" });
const showPassword = ref(false);

// 保存表单数据到 localStorage
function saveForm() {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
      username: form.value.username,
      password: form.value.password
    }));
  } catch (e) {
    console.error("保存表单数据失败:", e);
  }
}

// 从 localStorage 恢复表单数据
onMounted(() => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY);
    if (saved) {
      const parsed = JSON.parse(saved);
      if (parsed.username) form.value.username = parsed.username;
      if (parsed.password) form.value.password = parsed.password;
    }
  } catch (e) {
    console.error("恢复表单数据失败:", e);
  }
});

// 使用防抖保存，避免频繁写入
let saveTimer: ReturnType<typeof setTimeout> | null = null;
function debouncedSave() {
  if (saveTimer) clearTimeout(saveTimer);
  saveTimer = setTimeout(() => {
    saveForm();
  }, 300);
}

// 监听表单变化
watchEffect(() => {
  // 触发依赖收集
  form.value.username;
  form.value.password;
  debouncedSave();
});

// 路由离开前保存数据
onBeforeRouteLeave(() => {
  saveForm();
});

function onSubmit() {
  if (!form.value.username) return Toast.fail("请输入用户名");
  if (!form.value.password || form.value.password.length < 3)
    return Toast.fail("请输入至少 3 位密码");

  const res = auth.login(form.value.username.trim(), form.value.password);
  if (!res.success) return Toast.fail(res.message || "登录失败");

  // 登录成功后清除保存的表单数据
  localStorage.removeItem(STORAGE_KEY);
  Toast.success("登录成功");
  // navigate to the My page
  router.push({ path: "/my" });
}
</script>

<style scoped>
.page {
  padding: 16px;
}
.logo {
  display: flex;
  justify-content: center;
  margin-bottom: 3em;
  margin-top: 3em;
}
</style>
