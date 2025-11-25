<template>
  <van-nav-bar title="注册" left-text=" " />

  <div class="page">
        <div class="logo">
      <img src="@/assets/vue.svg" alt="" />
      <p>(logo)</p>
    </div>

    <van-form @submit.prevent="onSubmit">
      <van-field 
        v-model="form.username" 
        label="用户名" 
        placeholder="请输入用户名" 
        clearable 
        @update:value="saveForm"
      />
      <van-field 
        v-model="form.displayName" 
        label="昵称（可选）" 
        placeholder="请输入昵称" 
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
      <van-field
        v-model="form.confirmPassword"
        :type="showConfirmPassword ? 'text' : 'password'"
        label="确认密码"
        placeholder="请再次输入密码"
        clearable
        @update:value="saveForm"
      >
        <template #right-icon>
          <div @click.stop="showConfirmPassword = !showConfirmPassword" style="cursor: pointer; padding: 0 8px; display: flex; align-items: center; user-select: none;">
            <van-icon :name="showConfirmPassword ? 'eye-o' : 'closed-eye'" />
          </div>
        </template>
      </van-field>

      <div style="margin-top: 1.5em; margin-left: 2em; margin-right: 2em">
        <van-button block round type="primary" native-type="submit">注册</van-button>
      </div>
    </van-form>

    <div style="margin-top: 12px; text-align: center; font-size: 0.5em;">
      <router-link to="/my/login">已有账号？去登录</router-link>

    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { useRouter, onBeforeRouteLeave } from 'vue-router'
import { Toast } from 'vant'
import * as auth from '@/services/auth'

const router = useRouter()

const STORAGE_KEY = "jobmate_register_form";
const form = ref({ username: '', displayName: '', password: '', confirmPassword: '' })
const showPassword = ref(false)
const showConfirmPassword = ref(false)

// 保存表单数据到 localStorage
function saveForm() {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify({
      username: form.value.username,
      displayName: form.value.displayName,
      password: form.value.password,
      confirmPassword: form.value.confirmPassword
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
      if (parsed.displayName) form.value.displayName = parsed.displayName;
      if (parsed.password) form.value.password = parsed.password;
      if (parsed.confirmPassword) form.value.confirmPassword = parsed.confirmPassword;
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
watch(form, debouncedSave, { deep: true });

// 路由离开前保存数据
onBeforeRouteLeave(() => {
  saveForm();
});

function onSubmit() {
  if (!form.value.username) return Toast.fail('请输入用户名')
  if (!form.value.password || form.value.password.length < 3) return Toast.fail('密码至少 3 位')
  if (form.value.password !== form.value.confirmPassword) return Toast.fail('两次输入的密码不一致')

  const res = auth.register({
    username: form.value.username.trim(),
    password: form.value.password,
    displayName: form.value.displayName?.trim() || undefined,
  })

  if (!res.success) return Toast.fail(res.message || '注册失败')

  localStorage.removeItem(STORAGE_KEY);

  Toast.success({
    message: '注册成功，请登录',
    duration: 800,  // 可改
  })

  router.push('/my/login')
}

</script>

<style scoped>
.logo {
  display: flex;
  justify-content: center;
  margin-bottom: 3em;
  margin-top: 3em;
}
</style>
