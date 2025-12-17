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
                <van-cell :title="job.title" :label="job.company" :value="job.salary" />
                <template #right>
                  <van-button square text="编辑" type="primary" class="h-full" @click="openJobEdit(job)" />
                  <van-button square text="删除" type="danger" class="h-full" @click="handleDeleteJob(job.id)" />
                </template>
              </van-swipe-cell>
            </van-cell-group>
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
                <van-cell :title="act.title" :label="act.date + ' | ' + act.location" :value="act.status" />
                <template #right>
                  <van-button square text="编辑" type="primary" class="h-full" @click="openActivityEdit(act)" />
                  <van-button square text="删除" type="danger" class="h-full" @click="handleDeleteActivity(act.id)" />
                </template>
              </van-swipe-cell>
            </van-cell-group>
            <div v-if="activityList.length === 0" class="text-center text-gray-400 mt-10">暂无宣讲会数据</div>
          </div>
        </div>
      </van-popup>

      <!-- Job Edit Dialog -->
      <van-dialog v-model:show="showJobEdit" :title="editingJob?.id ? '编辑职位' : '发布职位'" show-cancel-button @confirm="saveJob">
        <van-form class="p-4">
          <van-field v-model="jobForm.title" label="职位名称" placeholder="请输入职位名称" />
          <van-field v-model="jobForm.company" label="公司名称" placeholder="请输入公司名称" />
          <van-field v-model="jobForm.salary" label="薪资范围" placeholder="例如: 15-25K" />
          <van-field v-model="jobForm.location" label="工作地点" placeholder="例如: 杭州" />
          <van-field v-model="jobForm.type" label="职位类型" placeholder="例如: 全职" />
        </van-form>
      </van-dialog>

      <!-- Activity Edit Dialog -->
      <van-dialog v-model:show="showActivityEdit" :title="editingActivity?.id ? '编辑宣讲会' : '发布宣讲会'" show-cancel-button @confirm="saveActivity">
        <van-form class="p-4">
          <van-field v-model="activityForm.title" label="标题" placeholder="请输入宣讲会标题" />
          <van-field v-model="activityForm.date" label="时间" placeholder="例如: 2024-05-20 14:00" />
          <van-field v-model="activityForm.location" label="地点" placeholder="请输入地点" />
          <van-field v-model="activityForm.status" label="状态" placeholder="例如: 报名中" />
        </van-form>
      </van-dialog>

    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, reactive, watch } from "vue";
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

// Edit State
const showJobEdit = ref(false);
const editingJob = ref<jobService.Job | null>(null);
const jobForm = reactive({ title: '', company: '', salary: '', location: '', type: '' });

const showActivityEdit = ref(false);
const editingActivity = ref<activityService.Activity | null>(null);
const activityForm = reactive({ title: '', date: '', location: '', status: '' });


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
});

// Fetch Data when popups open
watch(showUserMgr, (val) => { if(val) fetchUsers(); });
watch(showJobMgr, (val) => { if(val) fetchJobs(); });
watch(showActivityMgr, (val) => { if(val) fetchActivities(); });

async function fetchUsers() {
  try {
    const res = await userService.getUsers();
    if(res.code === 0) userList.value = res.data || [];
  } catch(e) { console.error(e); }
}

async function fetchJobs() {
  try {
    const res = await jobService.getJobs();
    if(res.code === 0) jobList.value = res.data || [];
  } catch(e) { console.error(e); }
}

async function fetchActivities() {
  try {
    const res = await activityService.getActivities();
    if(res.code === 0) activityList.value = res.data || [];
  } catch(e) { console.error(e); }
}

// Job Operations
function openJobEdit(job: jobService.Job | null) {
  editingJob.value = job;
  if (job) {
    Object.assign(jobForm, job);
  } else {
    Object.assign(jobForm, { title: '', company: '', salary: '', location: '', type: '' });
  }
  showJobEdit.value = true;
}

async function saveJob() {
  try {
    if (editingJob.value) {
      await jobService.updateJob(editingJob.value.id, jobForm);
      showSuccessToast('更新成功');
    } else {
      await jobService.createJob(jobForm);
      showSuccessToast('发布成功');
    }
    fetchJobs();
  } catch (e) { showToast('操作失败'); }
}

function handleDeleteJob(id: number) {
  showConfirmDialog({ title: '确认删除', message: '确定要删除这个职位吗？' })
    .then(async () => {
      await jobService.deleteJob(id);
      showSuccessToast('删除成功');
      fetchJobs();
    }).catch(() => {});
}

// Activity Operations
function openActivityEdit(act: activityService.Activity | null) {
  editingActivity.value = act;
  if (act) {
    Object.assign(activityForm, act);
  } else {
    Object.assign(activityForm, { title: '', date: '', location: '', status: '' });
  }
  showActivityEdit.value = true;
}

async function saveActivity() {
  try {
    if (editingActivity.value) {
      await activityService.updateActivity(editingActivity.value.id, activityForm);
      showSuccessToast('更新成功');
    } else {
      await activityService.createActivity(activityForm);
      showSuccessToast('发布成功');
    }
    fetchActivities();
  } catch (e) { showToast('操作失败'); }
}

function handleDeleteActivity(id: number) {
  showConfirmDialog({ title: '确认删除', message: '确定要删除这个宣讲会吗？' })
    .then(async () => {
      await activityService.deleteActivity(id);
      showSuccessToast('删除成功');
      fetchActivities();
    }).catch(() => {});
}

function onLogout() {
  auth.logout();
  user.value = null;
  showSuccessToast("已退出");
  router.push({ path: "/my/login" });
}
</script>
