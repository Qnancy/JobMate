<template>
  <div class="page">
    <!-- 顶部标语区域 - 优化间距和视觉层次 -->
    <div class="slogan-container">
      <h1 class="main-slogan">连接机遇，成就未来</h1>
      <p class="sub-slogan">从这里开始你的职业新篇章</p>
    </div>

    <!-- 表单区域 - 优化卡片感和输入框样式 -->
    <div class="form-card">
      <van-form @submit="onSubmit">
        <van-field
          v-model="form.username"
          left-icon="user"
          label="用户名"
          placeholder="请输入用户名"
          clearable
          @update:value="saveForm"
          class="custom-field"
        />

        <van-field
          v-model="form.password"
          :type="showPassword ? 'text' : 'password'"
          left-icon="lock"
          label="密码"
          placeholder="请输入密码"
          clearable
          @update:value="saveForm"
          class="custom-field"
        >
          <template #right-icon>
            <div class="password-toggle" @click.stop="showPassword = !showPassword">
              <van-icon :name="showPassword ? 'eye-o' : 'closed-eye'" color="#999" />
            </div>
          </template>
        </van-field>

        <div class="login-button-box">
          <van-button 
            block 
            type="primary" 
            native-type="submit" 
            class="login-button"
          >
            登录
          </van-button>
        </div>
      </van-form>

      <!-- 注册链接 - 优化颜色和交互 -->
      <div class="register-link">
        <router-link to="/my/register" class="register-btn">
          还没有账号？去注册
        </router-link>
      </div>
    </div>

    <!-- 分隔线和其他登录方式 - 优化间距和样式 -->
    <div class="other-login-container">
      <an-divider class="custom-divider">其他方式</an-divider>
      
      <div class="other-login-button">
        <ZjuLoginButton />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 原有逻辑保持不变，无需修改
import { ref, onMounted, watchEffect } from "vue";
import { useRouter, onBeforeRouteLeave } from "vue-router";
import { showToast } from "vant";
import * as auth from "@/services/auth";
import { Divider as AnDivider } from "ant-design-vue";
import ZjuLoginButton from "@/components/zju-login-button.vue";

const router = useRouter();

const STORAGE_KEY = "jobmate_login_form";
const CURRENT_KEY = "jobmate_current_user";
const form = ref({ username: "", password: "" });
const showPassword = ref(false);

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

let saveTimer: ReturnType<typeof setTimeout> | null = null;
function debouncedSave() {
  if (saveTimer) clearTimeout(saveTimer);
  saveTimer = setTimeout(() => {
    saveForm();
  }, 300);
}

watchEffect(() => {
  form.value.username;
  form.value.password;
  debouncedSave();
});

onBeforeRouteLeave(() => {
  saveForm();
});

async function onSubmit() {
  if (!form.value.username) return showToast("请输入用户名");
  if (!form.value.password || form.value.password.length < 3)
    return showToast("请输入至少 3 位密码");

  const res = await auth.login(form.value.username.trim(), form.value.password);
  if (res.code !== 0) return showToast(res.message || "登录失败");

  localStorage.removeItem(STORAGE_KEY);
  localStorage.setItem(CURRENT_KEY, JSON.stringify(res.data));
  showToast("登录成功");
  const target = res?.data?.role === 'admin' ? '/admin' : '/my';
  router.push({ path: target });
}
</script>

<style scoped>
/* 引入字体 */
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&display=swap');

.page {
  padding: 32px 16px;
  font-family: 'Noto Sans SC', sans-serif;
  background-color: #f8f9fa;
  height: 80vh;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 标语区域优化 */
.slogan-container {
  text-align: center;
  margin-bottom: 32px;
  width: 100%;
  max-width: 400px;
}

.main-slogan {
  font-size: 28px;
  font-weight: 700;
  color: #2d3748;
  margin-bottom: 8px;
  line-height: 1.3;
}

.sub-slogan {
  font-size: 15px;
  font-weight: 400;
  color: #718096;
  line-height: 1.5;
}

/* 表单卡片 - 增加立体感和留白 */
.form-card {
  width: 100%;
  max-width: 400px;
  background-color: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
}

/* 自定义输入框样式 */
.custom-field {
  --van-field-label-width: 64px;
  --van-field-input-height: 44px;
  --van-field-font-size: 15px;
  margin-bottom: 16px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fafafa;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.custom-field:focus-within {
  border-color: var(--van-primary-color);
  background-color: #ffffff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.1);
}

/* 密码切换按钮优化 */
.password-toggle {
  cursor: pointer;
  padding: 0 12px;
  display: flex;
  align-items: center;
  user-select: none;
  transition: color 0.2s ease;
}

.password-toggle:hover {
  color: var(--van-primary-color);
}

/* 登录按钮优化 */
.login-button-box {
  margin-top: 24px;
  margin-left: 0;
  margin-right: 0;
  display: flex;
  justify-content: center;
}

.login-button {
  border-radius: 8px;
  font-weight: 500;
  font-size: 16px;
  height: 48px;
  background-color: var(--van-primary-color);
  border: none;
  transition: all 0.3s ease;
}

/* 登录按钮悬停和点击效果 */
.login-button:hover {
  background-color: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.2);
}

.login-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.2);
}

/* 注册链接效果 */
.register-link {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
}

.register-btn {
  color: var(--van-primary-color);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
  padding: 4px 8px;
  border-radius: 4px;
}

.register-btn:hover {
  color: #2563eb;
  background-color: rgba(59, 130, 246, 0.08);
}

/* 其他登录方式区域优化 */
.other-login-container {
  width: 100%;
  max-width: 400px;
  margin-top: 24px;
  text-align: center;
}

/* 自定义分隔线 */
.custom-divider {
  --antd-divider-text-color: #718096;
  --antd-divider-font-size: 14px;
  --antd-divider-margin: 16px 0;
}


.other-login-button {
  display: flex;
  justify-content: center;
  margin-top: 16px;
  padding: 0 16px;
}


</style>