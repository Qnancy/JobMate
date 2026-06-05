<template>
  <van-nav-bar :title="title" left-arrow @click-left="emit('back')" />

  <div class="min-h-screen bg-[#f7f8fa] pb-28">
    <div class="mx-auto max-w-[min(820px,96vw)] px-3 pt-3 space-y-3">
      <van-search
        v-model="keyword"
        shape="round"
        background="#ffffff"
        :placeholder="searchPlaceholder"
        show-action
        clearable
        @search="onApply"
        @clear="onClear"
      >
        <template #action>
          <div class="text-[#1989fa] text-sm px-1" @click="onApply">搜索</div>
        </template>
      </van-search>

      <div v-if="$slots.toolbar" class="flex justify-end items-center gap-2">
        <slot name="toolbar" />
      </div>

      <van-pull-refresh v-model="refreshing" @refresh="emit('refresh')">
        <div class="bg-white rounded-2xl shadow-sm overflow-hidden min-h-[100px]">
          <slot />
        </div>
        <slot name="after-card" />
      </van-pull-refresh>
    </div>
  </div>
</template>

<script setup lang="ts">
const keyword = defineModel<string>('keyword', { default: '' })
const refreshing = defineModel<boolean>('refreshing', { default: false })

defineProps<{
  title: string
  searchPlaceholder: string
}>()

const emit = defineEmits<{
  back: []
  refresh: []
  applySearch: []
}>()

function onApply() {
  emit('applySearch')
}

function onClear() {
  emit('applySearch')
}
</script>
