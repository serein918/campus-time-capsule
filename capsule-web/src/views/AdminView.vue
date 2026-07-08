<script setup>
import { ref, onMounted } from 'vue'
import { getUserList, updateUserStatus, getCapsuleList, deleteCapsule, addNotice, deleteNotice, getStatistics } from '@/api/admin'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as echarts from 'echarts'

const activeTab = ref('statistics')
const stats = ref({})

// 用户管理
const users = ref([])
const userPage = ref(1)
const userTotal = ref(0)
const userKeyword = ref('')

// 胶囊管理
const capsules = ref([])
const capsulePage = ref(1)
const capsuleTotal = ref(0)
const capsuleKeyword = ref('')

// 公告管理
const noticeForm = ref({ title: '', content: '' })
const showNoticeDialog = ref(false)

onMounted(() => {
  loadStatistics()
})

async function loadStatistics() {
  try {
    const res = await getStatistics()
    stats.value = res.data
    // 渲染图表
    setTimeout(() => renderChart(), 100)
  } catch (e) {}
}

function renderChart() {
  const chartDom = document.getElementById('statsChart')
  if (!chartDom) return
  const chart = echarts.init(chartDom)
  chart.setOption({
    title: { text: '系统数据概览', left: 'center' },
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: '60%',
      data: [
        { value: stats.value.userCount || 0, name: '用户数' },
        { value: stats.value.capsuleCount || 0, name: '胶囊数' },
        { value: stats.value.commentCount || 0, name: '评论数' },
        { value: stats.value.noticeCount || 0, name: '公告数' }
      ]
    }]
  })
}

// 用户管理方法
async function loadUsers() {
  try {
    const res = await getUserList({ page: userPage.value, size: 10, keyword: userKeyword.value || undefined })
    users.value = res.data.records || []
    userTotal.value = res.data.total || 0
  } catch (e) {}
}

async function handleToggleUserStatus(userId, currentStatus) {
  const newStatus = currentStatus === 1 ? 0 : 1
  try {
    await updateUserStatus(userId, newStatus)
    ElMessage.success(newStatus === 1 ? '用户已启用' : '用户已禁用')
    loadUsers()
  } catch (e) {}
}

// 胶囊管理方法
async function loadCapsules() {
  try {
    const res = await getCapsuleList({ page: capsulePage.value, size: 10, keyword: capsuleKeyword.value || undefined })
    capsules.value = res.data.records || []
    capsuleTotal.value = res.data.total || 0
  } catch (e) {}
}

async function handleDeleteCapsule(id) {
  try {
    await ElMessageBox.confirm('确定删除该胶囊？', '提示', { type: 'warning' })
    await deleteCapsule(id)
    ElMessage.success('删除成功')
    loadCapsules()
  } catch (e) {}
}

// 公告管理方法
async function handleAddNotice() {
  if (!noticeForm.value.title || !noticeForm.value.content) {
    ElMessage.warning('请填写公告标题和内容')
    return
  }
  try {
    await addNotice(noticeForm.value)
    ElMessage.success('公告发布成功')
    showNoticeDialog.value = false
    noticeForm.value = { title: '', content: '' }
  } catch (e) {}
}

function handleTabChange(tab) {
  if (tab === 'users') loadUsers()
  else if (tab === 'capsules') loadCapsules()
  else if (tab === 'statistics') loadStatistics()
}
</script>

<template>
  <el-card>
    <template #header>
      <h3>管理员后台</h3>
    </template>
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 统计分析 -->
      <el-tab-pane label="统计分析" name="statistics">
        <el-row :gutter="20" style="margin-bottom: 20px;">
          <el-col :span="6">
            <el-statistic title="用户总数" :value="stats.userCount || 0" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="胶囊总数" :value="stats.capsuleCount || 0" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="评论总数" :value="stats.commentCount || 0" />
          </el-col>
          <el-col :span="6">
            <el-statistic title="公告总数" :value="stats.noticeCount || 0" />
          </el-col>
        </el-row>
        <div id="statsChart" style="width: 100%; height: 400px;"></div>
      </el-tab-pane>

      <!-- 用户管理 -->
      <el-tab-pane label="用户管理" name="users">
        <div style="margin-bottom: 16px;">
          <el-input v-model="userKeyword" placeholder="搜索用户名/昵称" style="width: 250px;" clearable>
            <template #append>
              <el-button @click="loadUsers">搜索</el-button>
            </template>
          </el-input>
        </div>
        <el-table :data="users" style="width: 100%;">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="username" label="用户名" width="150" />
          <el-table-column prop="nickname" label="昵称" width="150" />
          <el-table-column prop="role" label="角色" width="100" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
                {{ row.status === 1 ? '正常' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="注册时间" width="180" />
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button :type="row.status === 1 ? 'danger' : 'success'" link @click="handleToggleUserStatus(row.id, row.status)">
                {{ row.status === 1 ? '禁用' : '启用' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="display: flex; justify-content: center; margin-top: 20px;">
          <el-pagination v-if="userTotal > 0" :current-page="userPage" :page-size="10" :total="userTotal" layout="prev, pager, next" @current-change="(p) => { userPage = p; loadUsers() }" />
        </div>
      </el-tab-pane>

      <!-- 胶囊管理 -->
      <el-tab-pane label="胶囊管理" name="capsules">
        <div style="margin-bottom: 16px;">
          <el-input v-model="capsuleKeyword" placeholder="搜索胶囊标题" style="width: 250px;" clearable>
            <template #append>
              <el-button @click="loadCapsules">搜索</el-button>
            </template>
          </el-input>
        </div>
        <el-table :data="capsules" style="width: 100%;">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="visibility" label="可见性" width="100" />
          <el-table-column prop="viewCount" label="浏览" width="80" />
          <el-table-column prop="likeCount" label="点赞" width="80" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <el-button type="danger" link @click="handleDeleteCapsule(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div style="display: flex; justify-content: center; margin-top: 20px;">
          <el-pagination v-if="capsuleTotal > 0" :current-page="capsulePage" :page-size="10" :total="capsuleTotal" layout="prev, pager, next" @current-change="(p) => { capsulePage = p; loadCapsules() }" />
        </div>
      </el-tab-pane>

      <!-- 公告管理 -->
      <el-tab-pane label="公告管理" name="notices">
        <el-button type="primary" @click="showNoticeDialog = true" style="margin-bottom: 16px;">发布公告</el-button>
        <el-dialog v-model="showNoticeDialog" title="发布公告" width="500px">
          <el-form :model="noticeForm" label-width="80px">
            <el-form-item label="标题">
              <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
            </el-form-item>
            <el-form-item label="内容">
              <el-input v-model="noticeForm.content" type="textarea" :rows="5" placeholder="请输入公告内容" />
            </el-form-item>
          </el-form>
          <template #footer>
            <el-button @click="showNoticeDialog = false">取消</el-button>
            <el-button type="primary" @click="handleAddNotice">发布</el-button>
          </template>
        </el-dialog>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>
