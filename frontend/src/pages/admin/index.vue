<template>
  <van-nav-bar title="管理员中心" left-text=" " />

  <div class="min-h-screen bg-w pt-3 px-3 pb-8">
    <template v-if="user">
      <!-- Admin Profile Card -->
      <div class="relative mt-2 mx-auto max-w-[600px] rounded-[18px] px-[18px] pt-6 pb-8 text-center text-white overflow-hidden shadow-[0_22px_40px_rgba(16,59,133,0.25)] bg-[linear-gradient(135deg,#214b97_0%,#3a7adf_60%,#6cb1ff_100%)]">
        <div class="pointer-events-none absolute right-[-70px] top-[-50px] w-[220px] h-[220px] bg-[radial-gradient(circle,rgba(255,255,255,0.28)_0%,rgba(255,255,255,0)_70%)]"></div>
        <van-image
          class="border-2 border-white/55 shadow-[0_16px_36px_rgba(0,36,110,0.35)]"
          round
          width="80px"
          height="80px"
          fit="cover"
          :src="defaultAvatar"
        />
        <div class="mt-3 text-[18px] font-bold tracking-[0.3px]">{{ user.name }}</div>
        <div class="mt-1 text-[13px] opacity-90">系统管理员</div>
      </div>

      <!-- Admin Menu -->
      <div class="mt-4 mx-auto max-w-[640px] bg-white rounded-2xl overflow-hidden">
        <van-cell-group inset>
          <van-cell
            class="group text-[#1f2a44] text-[15px] font-semibold pl-3 pr-4 py-3 hover:bg-[#f5f8ff] hover:shadow-[0_10px_24px_rgba(0,28,84,0.12)] hover:-translate-y-[2px] transition-all duration-200"
            is-link
            @click="showUserMgr = true"
          >
            <template #title>
              <div class="flex items-center gap-2 text-left">
                <van-icon name="friends-o" class="text-[22px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[15px] font-semibold">用户管理</span>
              </div>
            </template>
            <template #label>
                <span class="text-xs text-gray-400">查看与管理所有用户信息</span>
            </template>
          </van-cell>
          <van-cell
            class="group text-[#1f2a44] text-[15px] font-semibold pl-3 pr-4 py-3 hover:bg-[#f5f8ff] hover:shadow-[0_10px_24px_rgba(0,28,84,0.12)] hover:-translate-y-[2px] transition-all duration-200"
            is-link
            @click="showJobMgr = true"
          >
            <template #title>
              <div class="flex items-center gap-2 text-left">
                <van-icon name="todo-list-o" class="text-[22px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[15px] font-semibold">职位管理</span>
              </div>
            </template>
             <template #label>
                <span class="text-xs text-gray-400">发布、编辑与删除职位信息</span>
            </template>
          </van-cell>
          <van-cell
            class="group text-[#1f2a44] text-[15px] font-semibold pl-3 pr-4 py-3 hover:bg-[#f5f8ff] hover:shadow-[0_10px_24px_rgba(0,28,84,0.12)] hover:-translate-y-[2px] transition-all duration-200"
            is-link
            @click="showActivityMgr = true"
          >
            <template #title>
              <div class="flex items-center gap-2 text-left">
                <van-icon name="bullhorn-o" class="text-[22px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[15px] font-semibold">宣讲会管理</span>
              </div>
            </template>
             <template #label>
                <span class="text-xs text-gray-400">发布、编辑与删除宣讲会信息</span>
            </template>
          </van-cell>
           <van-cell
            class="group text-[#1f2a44] text-[15px] font-semibold pl-3 pr-4 py-3 hover:bg-[#f5f8ff] hover:shadow-[0_10px_24px_rgba(0,28,84,0.12)] hover:-translate-y-[2px] transition-all duration-200"
            is-link
          >
            <template #title>
              <div class="flex items-center gap-2 text-left">
                <van-icon name="setting-o" class="text-[22px] text-[#2f69c8]" />
                <span class="text-[#1f2a44] text-[15px] font-semibold">系统设置</span>
              </div>
            </template>
             <template #label>
                <span class="text-xs text-gray-400">后续可添加白名单、角色权限等</span>
            </template>
          </van-cell>
        </van-cell-group>
      </div>

      <!-- Logout Button -->
      <div class="mt-4 mx-auto max-w-[640px]">
        <van-cell
          class="bg-white text-[#e45b5b] mx-2 my-3 rounded-[14px] shadow-[0_10px_24px_rgba(0,0,0,0.05)] border border-[#f1c7c7] transition duration-200 cursor-pointer hover:bg-[#fff4f4] hover:text-[#d63b3b] hover:shadow-[0_10px_24px_rgba(255,140,140,0.18)] hover:-translate-y-[2px]"
          center
          clickable
          @click="onLogout"
        >
          <template #title>
            <div class="text-[15px] font-bold text-center text-[#e45b5b]">退出登录</div>
          </template>
        </van-cell>
      </div>

      <!-- User Management Popup -->
      <van-popup v-model:show="showUserMgr" position="bottom" :style="{ height: '80%' }" round>
        <div class="p-4 h-full flex flex-col">
          <h3 class="text-lg font-bold mb-4">用户管理</h3>
          <div class="flex-1 overflow-y-auto">
            <van-list>
              <van-cell v-for="u in userList" :key="u.id" :title="u.name" :label="u.role" />
            </van-list>
            <div v-if="userList.length === 0" class="text-center text-gray-400 mt-10">暂无用户数据</div>
          </div>
          <van-button block type="primary" class="mt-4" @click="fetchUsers">刷新列表</van-button>
        </div>
      </van-popup>

      <!-- Job Management Popup -->
      <van-popup v-model:show="showJobMgr" position="bottom" :style="{ height: '80%' }" round>
        <div class="p-4 h-full flex flex-col">
          <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold">职位管理</h3>
            <van-button size="small" type="primary" icon="plus" @click="openJobEdit(null)">发布</van-button>
          </div>
          <div class="flex-1 overflow-y-auto">
            <van-cell-group>
              <van-swipe-cell v-for="job in jobList" :key="job.id">
                <van-cell :title="job.position" :label="`公司ID: ${job.company.id} | ${job.location || '暂无地点'}`" :value="recruitTypeMap[job.recruit_type] || job.recruit_type" />
                <template #right>
                  <van-button square text="编辑" type="primary" class="h-full" @click="openJobEdit(job)" />
                  <van-button square text="删除" type="danger" class="h-full" @click="handleDeleteJob(job.id)" />
                </template>
              </van-swipe-cell>
            </van-cell-group>
            <div ref="jobSentinel" class="h-6"></div>
            <div v-if="jobLoading" class="text-center text-gray-400 py-2">加载中...</div>
            <div v-else-if="!jobHasMore && jobList.length" class="text-center text-gray-400 py-2">没有更多</div>
            <div v-if="jobList.length === 0" class="text-center text-gray-400 mt-10">暂无职位数据</div>
          </div>
        </div>
      </van-popup>

      <!-- Activity Management Popup -->
      <van-popup v-model:show="showActivityMgr" position="bottom" :style="{ height: '80%' }" round>
        <div class="p-4 h-full flex flex-col">
           <div class="flex justify-between items-center mb-4">
            <h3 class="text-lg font-bold">宣讲会管理</h3>
            <van-button size="small" type="primary" icon="plus" @click="openActivityEdit(null)">发布</van-button>
          </div>
          <div class="flex-1 overflow-y-auto">
             <van-cell-group>
              <van-swipe-cell v-for="act in activityList" :key="act.id">
                <van-cell :title="act.title" :label="`${act.time} | ${act.location || '暂无地点'}`" :value="act.status || ''" />
                <template #right>
                  <van-button square text="编辑" type="primary" class="h-full" @click="openActivityEdit(act)" />
                  <van-button square text="删除" type="danger" class="h-full" @click="handleDeleteActivity(act.id)" />
                </template>
              </van-swipe-cell>
            </van-cell-group>
            <div ref="activitySentinel" class="h-6"></div>
            <div v-if="activityLoading" class="text-center text-gray-400 py-2">加载中...</div>
            <div v-else-if="!activityHasMore && activityList.length" class="text-center text-gray-400 py-2">没有更多</div>
            <div v-if="activityList.length === 0" class="text-center text-gray-400 mt-10">暂无宣讲会数据</div>
          </div>
        </div>
      </van-popup>

      <!-- Job Edit Dialog -->
      <van-dialog v-model:show="showJobEdit" :title="editingJob?.id ? '编辑职位' : '发布职位'" show-cancel-button @confirm="saveJob">
        <van-form class="p-4">
          <van-field v-model="jobForm.position" label="职位名称" placeholder="请输入职位名称" />
          <van-field v-model.number="jobForm.company_id" type="digit" label="公司ID" placeholder="请输入公司ID" />
          
          <van-field
            v-model="recruitTypeMap[jobForm.recruit_type]"
            is-link
            readonly
            name="recruit_type"
            label="招聘类型"
            placeholder="点击选择招聘类型"
            @click="showRecruitTypePicker = true"
          />
          <van-popup v-model:show="showRecruitTypePicker" position="bottom">
            <van-picker
              :columns="recruitTypeColumns"
              @confirm="onConfirmRecruitType"
              @cancel="showRecruitTypePicker = false"
            />
          </van-popup>

          <van-field v-model="jobForm.location" label="工作地点" placeholder="请输入工作地点" />
          <van-field v-model="jobForm.link" label="投递链接" placeholder="请输入投递链接" />
          <van-field v-model="jobForm.extra" label="备注" placeholder="其他信息" />
        </van-form>
      </van-dialog>

      <!-- Activity Edit Dialog -->
      <van-dialog v-model:show="showActivityEdit" :title="editingActivity?.id ? '编辑宣讲会' : '发布宣讲会'" show-cancel-button @confirm="saveActivity">
        <van-form class="p-4">
          <van-field v-model.number="activityForm.company_id" type="digit" label="公司ID" placeholder="请输入公司ID" />
          <van-field v-model="activityForm.title" label="标题" placeholder="请输入宣讲会标题" />
          <van-field v-model="activityForm.time" label="时间" placeholder="例如: 2024-05-20 14:00" />
          <van-field v-model="activityForm.location" label="地点" placeholder="请输入地点" />
          <van-field v-model="activityForm.link" label="链接" placeholder="请输入报名链接" />
          <van-field v-model="activityForm.extra" label="备注" placeholder="其他信息" />
          <van-field v-model="activityForm.status" label="状态" placeholder="例如: 报名中" />
        </van-form>
      </van-dialog>

    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, reactive, watch, nextTick } from "vue";
import { useRouter } from "vue-router";
import { showToast, showSuccessToast, showConfirmDialog } from "vant";
import * as auth from "@/services/auth";
import * as userService from "@/services/user";
import * as jobService from "@/services/job";
import * as activityService from "@/services/activity";

const router = useRouter();
const defaultAvatar = '/Zhejiang_University_Logo.svg.png';

const user = ref<auth.User | null>(null);

// State for Popups
const showUserMgr = ref(false);
const showJobMgr = ref(false);
const showActivityMgr = ref(false);

// Data Lists
const userList = ref<userService.User[]>([]);
const jobList = ref<jobService.Job[]>([]);
const activityList = ref<activityService.Activity[]>([]);

// Infinite scroll state
const jobPage = ref(1);
const jobPageSize = ref(10);
const jobLoading = ref(false);
const jobHasMore = ref(true);

const activityPage = ref(1);
const activityPageSize = ref(10);
const activityLoading = ref(false);
const activityHasMore = ref(true);

const jobSentinel = ref<HTMLElement | null>(null);
const activitySentinel = ref<HTMLElement | null>(null);
let jobObserver: IntersectionObserver | null = null;
let activityObserver: IntersectionObserver | null = null;

// Edit State
const showJobEdit = ref(false);
const editingJob = ref<jobService.Job | null>(null);
type JobForm = {
  company_id: number;
  recruit_type: jobService.JobType;
  position: string;
  link: string;
  location: string;
  extra: string;
};

const jobForm = reactive<JobForm>({
  company_id: 0,
  recruit_type: 'INTERN',
  position: '',
  link: '',
  location: '',
  extra: ''
});

const recruitTypeMap: Record<string, string> = {
  INTERN: '实习',
  CAMPUS: '校招',
  EXPERIENCED: '社招'
};

const showRecruitTypePicker = ref(false);
const recruitTypeColumns = [
  { text: '实习', value: 'INTERN' },
  { text: '校招', value: 'CAMPUS' },
  { text: '社招', value: 'EXPERIENCED' },
];
const onConfirmRecruitType = ({ selectedOptions }: any) => {
  jobForm.recruit_type = selectedOptions[0].value;
  showRecruitTypePicker.value = false;
};

const showActivityEdit = ref(false);
const editingActivity = ref<activityService.Activity | null>(null);

type ActivityForm = {
  company_id: number;
  title: string;
  time: string;
  link: string;
  location: string;
  extra: string;
  status: string;
};

const activityForm = reactive<ActivityForm>({
  company_id: 0,
  title: '',
  time: '',
  link: '',
  location: '',
  extra: '',
  status: '',
});


onMounted(() => {
    const currentUser = auth.currentUser();
    if (!currentUser) {
        showToast("请先登录");
        router.push({ path: "/my/login" });
        return;
    }
    if (currentUser.role !== 'admin') {
        showToast("无权访问管理员后台");
        router.push({ path: "/" });
        return;
    }
    user.value = currentUser;

  setupJobObserver();
  setupActivityObserver();
});

function setupJobObserver() {
  if (jobObserver) jobObserver.disconnect();
  jobObserver = new IntersectionObserver((entries) => {
    const entry = entries.find((e) => e.isIntersecting);
    if (!entry) return;
    fetchMoreJobs();
  });
  if (jobSentinel.value) jobObserver.observe(jobSentinel.value);
}

function setupActivityObserver() {
  if (activityObserver) activityObserver.disconnect();
  activityObserver = new IntersectionObserver((entries) => {
    const entry = entries.find((e) => e.isIntersecting);
    if (!entry) return;
    fetchMoreActivities();
  });
  if (activitySentinel.value) activityObserver.observe(activitySentinel.value);
}

// Fetch Data when popups open
watch(showUserMgr, (val) => { if (val) fetchUsers(); });

watch(showJobMgr, async (val) => {
  if (val) {
    jobPage.value = 1;
    jobHasMore.value = true;
    jobList.value = [];
    await nextTick();
    if (jobSentinel.value) jobObserver?.observe(jobSentinel.value);
    fetchMoreJobs();
  } else {
    if (jobSentinel.value) jobObserver?.unobserve(jobSentinel.value);
  }
});

watch(showActivityMgr, async (val) => {
  if (val) {
    activityPage.value = 1;
    activityHasMore.value = true;
    activityList.value = [];
    await nextTick();
    if (activitySentinel.value) activityObserver?.observe(activitySentinel.value);
    fetchMoreActivities();
  } else {
    if (activitySentinel.value) activityObserver?.unobserve(activitySentinel.value);
  }
});

async function fetchUsers() {
  try {
    const res = await userService.getUsers();
    if(res.code === 0) userList.value = res.data || [];
  } catch(e) { console.error(e); }
}

async function fetchMoreJobs() {
  if (jobLoading.value || !jobHasMore.value) return;

  jobLoading.value = true;
  try {
    const res = await jobService.getJob({
      page: jobPage.value,
      page_size: jobPageSize.value,
    });

    if (res.code === 0) {
      const list = res.data || [];
      jobList.value.push(...list);
      if (list.length < jobPageSize.value) {
        jobHasMore.value = false;
      } else {
        jobPage.value += 1;
      }
    }
  } catch(e) { console.error(e); }
  jobLoading.value = false;
}

async function fetchMoreActivities() {
  if (activityLoading.value || !activityHasMore.value) return;

  activityLoading.value = true;
  try {
    const res = await activityService.getActivities({
      page: activityPage.value,
      page_size: activityPageSize.value,
    });

    if (res.code === 0) {
      const list = res.data || [];
      activityList.value.push(...list);
      if (list.length < activityPageSize.value) {
        activityHasMore.value = false;
      } else {
        activityPage.value += 1;
      }
    }
  } catch(e) { console.error(e); }
  activityLoading.value = false;
}

// Job Operations
function openJobEdit(job: jobService.Job | null) {
  editingJob.value = job;
  if (job) {
    Object.assign(jobForm, {
      company_id: job.company.id,
      recruit_type: job.recruit_type,
      position: job.position,
      link: job.link || '',
      location: job.location || '',
      extra: job.extra || ''
    });
  } else {
    Object.assign(jobForm, {
      company_id: 0,
      recruit_type: 'INTERN',
      position: '',
      link: '',
      location: '',
      extra: ''
    });
  }
  showJobEdit.value = true;
}

async function saveJob() {
  try {
    // Convert empty strings to null for optional fields if needed, or keep as is.
    // Based on curl example, location can be null.
    const payload: jobService.JobPayload = {
      company_id: Number(jobForm.company_id) || 0,
      recruit_type: jobForm.recruit_type,
      position: jobForm.position,
      link: jobForm.link || null,
      location: jobForm.location || null,
      extra: jobForm.extra || null,
    };

    if (editingJob.value) {
      await jobService.updateJob(editingJob.value.id, payload);
      showSuccessToast('更新成功');
    } else {
      await jobService.createJob(payload);
      showSuccessToast('发布成功');
    }
    jobPage.value = 1;
    jobHasMore.value = true;
    jobList.value = [];
    await fetchMoreJobs();
  } catch (e) { showToast('操作失败'); }
}

function handleDeleteJob(id: number) {
  showConfirmDialog({ title: '确认删除', message: '确定要删除这个职位吗？' })
    .then(async () => {
      await jobService.deleteJob(id);
      showSuccessToast('删除成功');
      jobPage.value = 1;
      jobHasMore.value = true;
      jobList.value = [];
      await fetchMoreJobs();
    }).catch(() => {});
}

// Activity Operations
function openActivityEdit(act: activityService.Activity | null) {
  editingActivity.value = act;
  if (act) {
    Object.assign(activityForm, {
      company_id: act.company.id,
      title: act.title,
      time: act.time,
      link: act.link || '',
      location: act.location || '',
      extra: act.extra || '',
      status: act.status || '',
    });
  } else {
    Object.assign(activityForm, {
      company_id: 0,
      title: '',
      time: '',
      link: '',
      location: '',
      extra: '',
      status: '',
    });
  }
  showActivityEdit.value = true;
}

async function saveActivity() {
  try {
    const payload: activityService.ActivityPayload = {
      company_id: Number(activityForm.company_id) || 0,
      title: activityForm.title,
      time: activityForm.time,
      link: activityForm.link || null,
      location: activityForm.location || null,
      extra: activityForm.extra || null,
      status: activityForm.status || undefined,
    };

    if (editingActivity.value) {
      await activityService.updateActivity(editingActivity.value.id, payload);
      showSuccessToast('更新成功');
    } else {
      await activityService.createActivity(payload);
      showSuccessToast('发布成功');
    }
    activityPage.value = 1;
    activityHasMore.value = true;
    activityList.value = [];
    await fetchMoreActivities();
  } catch (e) { showToast('操作失败'); }
}

function handleDeleteActivity(id: number) {
  showConfirmDialog({ title: '确认删除', message: '确定要删除这个宣讲会吗？' })
    .then(async () => {
      await activityService.deleteActivity(id);
      showSuccessToast('删除成功');
      activityPage.value = 1;
      activityHasMore.value = true;
      activityList.value = [];
      await fetchMoreActivities();
    }).catch(() => {});
}

function onLogout() {
  auth.logout();
  user.value = null;
  showSuccessToast("已退出");
  router.push({ path: "/my/login" });
}

onUnmounted(() => {
  jobObserver?.disconnect();
  activityObserver?.disconnect();
});
</script>
