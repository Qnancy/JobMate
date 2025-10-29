<template>
  <van-nav-bar title="登录" left-text=" " />

  <div class="page">
    <div class="logo">
      <img src="@/assets/vue.svg" alt="" />
      <p>(logo)</p>
    </div>

    <van-form @submit="onSubmit">
      <van-field
        v-model:value="form.username"
        label="用户名"
        placeholder="请输入用户名"
        clearable
      />
      <van-field
        v-model:value="form.password"
        type="password"
        label="密码"
        placeholder="请输入密码"
        clearable
      />

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
import { ref } from "vue";
import { useRouter } from "vue-router";
import { Toast } from "vant";
import * as auth from "@/services/auth";

const router = useRouter();

const form = ref({ username: "", password: "" });

function onSubmit() {
  if (!form.value.username) return Toast.fail("请输入用户名");
  if (!form.value.password || form.value.password.length < 3)
    return Toast.fail("请输入至少 3 位密码");

  const res = auth.login(form.value.username.trim(), form.value.password);
  if (!res.success) return Toast.fail(res.message || "登录失败");

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
