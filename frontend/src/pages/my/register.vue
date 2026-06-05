<template>

  <div class="page">
    <!-- Logo区域优化 - 调整大小和间距 -->
    <div class="logo-container">
      <div class="logo">
        <img src="/Zhejiang_University_Logo.svg.png" alt="Logo" class="logo-img" />
        <p class="logo-text">注册页</p>
      </div>
    </div>

    <!-- 表单卡片 - 增加立体感和统一风格 -->
    <div class="form-card">
      <van-form @submit="onSubmit">
        <!-- 用户名输入框 -->
        <van-field 
          v-model="form.username" 
          label="用户名" 
          placeholder="请输入用户名" 
          clearable 
          class="custom-field"
          required
        />
        
        <!-- 密码输入框 -->
        <van-field
          v-model="form.password"
          :type="showPassword ? 'text' : 'password'"
          label="密码"
          placeholder="请输入至少6位密码"
          clearable
          class="custom-field"
          required
        >
          <template #right-icon>
            <div class="password-toggle" @click.stop="showPassword = !showPassword">
              <van-icon :name="showPassword ? 'eye-o' : 'closed-eye'" color="#999" />
            </div>
          </template>
        </van-field>
        
        <!-- 确认密码输入框 -->
        <van-field
          v-model="form.confirmPassword"
          :type="showConfirmPassword ? 'text' : 'password'"
          label="确认密码"
          placeholder="请再次输入密码"
          clearable
          class="custom-field"
          required
        >
          <template #right-icon>
            <div class="password-toggle" @click.stop="showConfirmPassword = !showConfirmPassword">
              <van-icon :name="showConfirmPassword ? 'eye-o' : 'closed-eye'" color="#999" />
            </div>
          </template>
        </van-field>

        <!-- 注册按钮 -->
        <div class="register-button-box">
          <van-button 
            block 
            round 
            type="primary" 
            native-type="submit"
            class="register-button"
          >
            注册
          </van-button>
        </div>
      </van-form>

      <!-- 登录链接 -->
      <div class="login-link">
        <router-link to="/my/login" class="login-btn">
          已有账号？去登录
        </router-link>
      </div>
      <div class="login-link mt-2">
        <router-link to="/my/register-admin" class="login-btn">
          管理员注册入口
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// 原有逻辑保持不变，仅增加返回按钮功能
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { showToast, showSuccessToast, showFailToast } from 'vant'
import * as auth from '@/services/auth'
import { isSuccessResponse } from '@/utils/request'

const router = useRouter()

const LEGACY_REGISTER_FORM_KEY = 'jobmate_register_form'
const form = ref({ username: '', password: '', confirmPassword: '' })
const showPassword = ref(false)
const showConfirmPassword = ref(false)

try {
  localStorage.removeItem(LEGACY_REGISTER_FORM_KEY)
} catch {
  /* ignore */
}

async function onSubmit() {
  if (!form.value.username) return showToast({ type: 'fail', message: '用户名不能为空' });
  if (!form.value.password || form.value.password.length < 6) return showToast('密码长度不能少于6位')
  if (form.value.password !== form.value.confirmPassword) return showToast('两次输入的密码不一致')
  
  console.log('注册请求发送中:', form.value)
  const res = await auth.register(
    form.value.username.trim(),
    form.value.password.trim(),
    "USER"
  ).catch((e) => {
    console.error('注册请求失败:', e)
    return { code: 500, message: '网络错误，请稍后重试', data: null }
  })

  if (!isSuccessResponse(res)) return showFailToast(res.message || '注册失败')

  showSuccessToast({
    message: '注册成功，请登录',
    duration: 800,
  })

  router.push('/my/login')
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&display=swap');


.page {
  height: 80vh;
  padding: 16px;
  font-family: 'Noto Sans SC', sans-serif;
  background-color: #f8f9fa;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
  align-items: center;
}


.custom-nav-bar {
  --van-nav-bar-background-color: #ffffff;
  --van-nav-bar-title-text-color: #2d3748;
  --van-nav-bar-title-font-size: 18px;
  --van-nav-bar-title-font-weight: 500;
  --van-nav-bar-left-text-color: var(--van-primary-color);
  --van-nav-bar-left-font-size: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

/* Logo区域优化 */
.logo-container {
  margin: 24px 0 32px;
  display: flex;
  justify-content: center;
  width: 100%;
}

.logo {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo-img {
  width: 64px;
  height: 64px;
  object-fit: contain;
  margin-bottom: 8px;
}

.logo-text {
  font-size: 14px;
  color: #718096;
  font-weight: 400;
}


.form-card {
  width: 100%;
  max-width: 400px;
  background-color: #ffffff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  box-sizing: border-box;
}


.custom-field {
  --van-field-label-width: 80px;
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


.register-button-box {
  margin-top: 24px;
  margin-left: 0;
  margin-right: 0;
}

.register-button {
  border-radius: 8px;
  font-weight: 500;
  font-size: 16px;
  height: 48px;
  background-color: var(--van-primary-color);
  border: none;
  transition: all 0.3s ease;
}

.register-button:hover {
  background-color: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(59, 130, 246, 0.2);
}

.register-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 4px rgba(59, 130, 246, 0.2);
}


.login-link {
  margin-top: 16px;
  text-align: center;
  font-size: 14px;
}

.login-btn {
  color: var(--van-primary-color);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s ease;
  padding: 4px 8px;
  border-radius: 4px;
}

.login-btn:hover {
  color: #2563eb;
  background-color: rgba(59, 130, 246, 0.08);
}
</style>