<script setup lang="ts">
// import HelloWorld from './components/HelloWorld.vue'
import { ref } from "vue";
import { RouterView } from "vue-router";
</script>

<template>
  <RouterView />
  <van-tabbar route>
    <van-tabbar-item to="/" icon="home-o">主页</van-tabbar-item>
    <van-tabbar-item to="/my" icon="friends-o">我的</van-tabbar-item>
  </van-tabbar>
</template>

<style scoped>
</style>
