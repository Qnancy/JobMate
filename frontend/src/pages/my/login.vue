<template>
  <div class="page">
    <div class="slogan-container">
      <h1 class="main-slogan">连接机遇，成就未来</h1>
      <p class="sub-slogan">从这里开始你的职业新篇章</p>
    </div>

    <van-form @submit="onSubmit">
      <van-field
        v-model="form.username"
        left-icon="user"
        label="用户名"
        placeholder="请输入用户名"
        clearable
        @update:value="saveForm"
      >

      </van-field>
      <van-field
        v-model="form.password"
        :type="showPassword ? 'text' : 'password'"
        left-icon="lock"
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

      <div class="login-button-box">
        <van-button block  type="primary" native-type="submit" class="login-button"
          >登录</van-button
        >
      </div>
    </van-form>

    <div class="login-button-box">
      <van-button block  type="success" class="login-button"
        >统一身份认证登录</van-button
      >
    </div>

    <div class="register-link">
      <router-link to="/my/register">还没有账号？去注册</router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watchEffect } from "vue";
import { useRouter, onBeforeRouteLeave } from "vue-router";
import { showToast, Toast } from "vant";
import * as auth from "@/services/auth";

const router = useRouter();

const STORAGE_KEY = "jobmate_login_form";
const CURRENT_KEY = "jobmate_current_user";
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

async function onSubmit() {
  if (!form.value.username) return showToast("请输入用户名");
  if (!form.value.password || form.value.password.length < 3)
    return showToast("请输入至少 3 位密码");

  const res = await auth.login(form.value.username.trim(), form.value.password);
  if (res.code !== 0) return showToast(res.message || "登录失败");

  // 登录成功后清除保存的表单数据
  localStorage.removeItem(STORAGE_KEY);
  localStorage.setItem(CURRENT_KEY, JSON.stringify(res.data));
  showToast("登录成功");
  // navigate to the My page
  router.push({ path: "/my" });
}
</script>

<style scoped>
/* 引入字体 */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&display=swap');

.page {
  padding: 16px;
  font-family: 'Noto Sans SC', sans-serif;
}
.slogan-container {
  text-align: center;
}
.main-slogan {
  font-size: 26px;
  font-weight: 700;
  color: #333;
}
.sub-slogan {
  font-size: 14px;
  font-weight: 300;
  color: #888;
}
.login-button-box {
  margin-top: 24px;
  text-align: center;
  margin-left: 16px;
  margin-right: 16px;
}
.login-button-box .login-button {
  border-radius: 8px;
  font-weight: 500;
}
.register-link {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
}
.register-link a {
  color: #888;
  text-decoration: none;
}
</style>
