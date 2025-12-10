<template>
  <div class="min-h-screen bg-gradient-to-b from-sky-50 to-white px-4 py-8 flex flex-col items-center">
    <div class="w-full max-w-4xl bg-white rounded-2xl shadow-lg border border-slate-100 p-6">
      <header class="flex items-center justify-between mb-6">
        <div>
          <p class="text-sm text-slate-500">管理员中心</p>
          <h1 class="text-2xl font-bold text-slate-800">后台面板</h1>
        </div>
        <router-link to="/" class="text-sm text-sky-600 hover:text-sky-700">返回首页</router-link>
      </header>

      <section class="grid gap-4 sm:grid-cols-2">
        <div class="rounded-xl border border-slate-100 p-4 shadow-sm">
          <h2 class="text-lg font-semibold text-slate-800 mb-2">用户管理</h2>
          <p class="text-sm text-slate-500">待接入后台接口后展示统计与列表。</p>
        </div>
        <div class="rounded-xl border border-slate-100 p-4 shadow-sm">
          <h2 class="text-lg font-semibold text-slate-800 mb-2">职位/审核</h2>
          <p class="text-sm text-slate-500">占位模块，后端就绪后对接。</p>
        </div>
        <div class="rounded-xl border border-slate-100 p-4 shadow-sm">
          <h2 class="text-lg font-semibold text-slate-800 mb-2">数据概览</h2>
          <p class="text-sm text-slate-500">可放置图表与指标。</p>
        </div>
        <div class="rounded-xl border border-slate-100 p-4 shadow-sm">
          <h2 class="text-lg font-semibold text-slate-800 mb-2">系统设置</h2>
          <p class="text-sm text-slate-500">后续可添加白名单、角色权限等。</p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
// 占位页：等待后端接口接入后填充具体内容
</script>
