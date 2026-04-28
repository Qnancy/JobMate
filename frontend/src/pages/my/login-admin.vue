<template>
  <div class="page">
    <div class="slogan-container">
      <h1 class="main-slogan">管理员登录</h1>
      <p class="sub-slogan">请输入管理员账号以进入后台</p>
    </div>

    <div class="form-card">
      <van-form @submit="onSubmit">
        <van-field
          v-model="form.username"
          left-icon="user"
          label="用户名"
          placeholder="请输入管理员用户名"
          clearable
          @update:value="saveForm"
          class="custom-field"
        />

        <van-field
          v-model="form.password"
          :type="showPassword ? 'text' : 'password'"
          left-icon="lock"
          label="密码"
          placeholder="请输入管理员密码"
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

        <van-field
          v-model="form.adminSecret"
          :type="showAdminSecret ? 'text' : 'password'"
          left-icon="shield-o"
          label="管理员密钥"
          placeholder="请输入管理员密钥"
          clearable
          @update:value="saveForm"
          class="custom-field"
        >
          <template #right-icon>
            <div class="password-toggle" @click.stop="showAdminSecret = !showAdminSecret">
              <van-icon :name="showAdminSecret ? 'eye-o' : 'closed-eye'" color="#999" />
            </div>
          </template>
        </van-field>

        <div class="login-button-box">
          <van-button block type="primary" native-type="submit" class="login-button">
            管理员登录
          </van-button>
        </div>
      </van-form>

      <div class="register-link">
        <router-link to="/my/register-admin" class="register-btn">
          还没有管理员账号？去注册
        </router-link>
      </div>
      <div class="register-link">
        <router-link to="/my/login" class="register-btn">
          普通用户登录
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watchEffect } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { showToast } from 'vant'
import * as auth from '@/services/auth'
import { isSuccessResponse } from '@/utils/request'

const router = useRouter()

const STORAGE_KEY = 'jobmate_login_admin_form'
const form = ref({ username: '', password: '', adminSecret: '' })
const showPassword = ref(false)
const showAdminSecret = ref(false)

function saveForm() {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
      username: form.value.username,
      password: form.value.password,
      adminSecret: form.value.adminSecret,
    }))
  } catch (e) {
    console.error('保存表单数据失败:', e)
  }
}

onMounted(() => {
  try {
    const saved = localStorage.getItem(STORAGE_KEY)
    if (!saved) return
    const parsed = JSON.parse(saved)
    if (parsed.username) form.value.username = parsed.username
    if (parsed.password) form.value.password = parsed.password
    if (parsed.adminSecret) form.value.adminSecret = parsed.adminSecret
  } catch (e) {
    console.error('恢复表单数据失败:', e)
  }
})

let saveTimer: ReturnType<typeof setTimeout> | null = null
function debouncedSave() {
  if (saveTimer) clearTimeout(saveTimer)
  saveTimer = setTimeout(() => {
    saveForm()
  }, 300)
}

watchEffect(() => {
  form.value.username
  form.value.password
  form.value.adminSecret
  debouncedSave()
})

onBeforeRouteLeave(() => {
  saveForm()
})

async function onSubmit() {
  if (!form.value.username) return showToast('请输入用户名')
  if (!form.value.password || form.value.password.length < 6) return showToast('请输入至少 6 位密码')
  if (!form.value.adminSecret.trim()) return showToast('请输入管理员密钥')

  try {
    const res = await auth.login(
      form.value.username.trim(),
      form.value.password,
      form.value.adminSecret.trim(),
    )
    if (!isSuccessResponse(res)) {
      if (res.message?.trim()) showToast(res.message)
      return
    }

    if (res.data?.role !== 'ADMIN') {
      auth.logout()
      return showToast('该账号不是管理员')
    }

    localStorage.removeItem(STORAGE_KEY)
    showToast('登录成功')
    router.push({ path: '/admin' })
  } catch {
    showToast('登录请求失败，请稍后重试')
  }
}
</script>

<style scoped>
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

.login-button-box {
  margin-top: 24px;
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
}

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
</style>
