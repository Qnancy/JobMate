<template>
  <van-list
    v-model:loading="loading"
    :finished="finished"
    finished-text="没有更多了"
    @load="onLoad"
    class="common-list"
  >
    <div v-for="item in props.list" :key="item.id" class="card-wrapper">
      <!-- 如果是职位，使用复杂卡片 -->
      <van-card
        v-if="item.company"
        :price="item.salary"
        :desc="`${item.company} ${item.companySize}`"
        :title="item.title"
        @click="() => router.push({ path: `/info/job/${item.id}` })"
      >
        <template #tags>
          <van-tag v-for="(tag, index) in item.tags" :key="index"  plain size="medium" class="job-tag">{{ tag }}</van-tag>
        </template>
        <template #footer>
          <div class="card-footer-info">
            <div class="hr-info">
              <span>{{ item.hr.name }} · {{ item.hr.role }}</span>
            </div>
            <div class="location-info">
              <span>{{ item.location.district }}</span>
              <span>{{ item.location.area }}</span>
            </div>
          </div>
        </template>
      </van-card>
      <!-- 如果是活动，使用简单卡片 -->
      <van-card
        v-else
        :title="item.title"
        :desc="item.subTitle"
        @click="() => router.push({ path: `/info/activity/${item.id}` })"
      >
        <template #tags>
          <van-tag plain type="primary">{{ item.tag }}</van-tag>
        </template>
      </van-card>
    </div>
  </van-list>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const props = defineProps({
  list: {
    type: Array,
    required: true,
  },
});

const loading = ref(false);
const finished = ref(true); 

function onLoad() {
  loading.value = false;
}

</script>

<style>
.common-list {
  padding: 10px;
  background-color: #f7f8fa;
}
.card-wrapper {
  margin-bottom: 10px;
  border-radius: 8px;
  overflow: hidden;
  background-color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}

.card-wrapper .van-card {
  padding: 16px;
  background-color: #fff;
}

.card-wrapper .van-card:hover {
  background-color: #f9f9f9;
  cursor: pointer;
}

.card-wrapper .van-card__title {
  font-size: 18px;
  font-weight: bold;
  padding-top: 2px;
  margin-bottom: 10px;
}
.card-wrapper .van-card__price {
  color: #00c2b3;
  font-size: 16px;
  font-weight: bold;
}
.card-wrapper .van-card__desc {
  margin-top: 4px;
  margin-bottom: 10px;
}
.card-wrapper .van-card__tags {
  display: flex;
  flex-wrap: wrap;
}
.job-tag {
  margin-right: 8px;
  margin-bottom: 8px;
}
.card-footer-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 10px;
  margin-top: 10px;
  border-top: 1px solid #f0f0f0;
  font-size: 12px;
  color: #666;
}
</style>
