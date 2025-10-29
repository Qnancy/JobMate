<template>
  <van-nav-bar title="注册" left-text=" " />

  <div class="page">
        <div class="logo">
      <img src="@/assets/vue.svg" alt="" />
      <p>(logo)</p>
    </div>

    <van-form @submit="onSubmit">
      <van-field v-model:value="form.username" label="用户名" placeholder="请输入用户名" clearable />
      <van-field v-model:value="form.displayName" label="昵称（可选）" placeholder="请输入昵称" clearable />
      <van-field v-model:value="form.password" type="password" label="密码" placeholder="请输入密码" clearable />
      <van-field v-model:value="form.confirmPassword" type="password" label="确认密码" placeholder="请再次输入密码" clearable />

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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Toast } from 'vant'
import * as auth from '@/services/auth'

const router = useRouter()

const form = ref({ username: '', displayName: '', password: '', confirmPassword: '' })

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

  Toast.success('注册成功，请登录')
  router.push({ path: '/my/login' })
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
