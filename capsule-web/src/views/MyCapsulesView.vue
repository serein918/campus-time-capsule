<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyCapsules, deleteCapsule } from '@/api/capsule'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const capsules = ref([])
const currentPage = ref(1)
const total = ref(0)

async function loadData() {
  try {
    const res = await getMyCapsules({ page: currentPage.value, size: 10 })
    capsules.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {}
}

onMounted(() => {
  loadData()
})

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除这个时光胶囊吗？删除后不可恢复。', '提示', { type: 'warning' })
    await deleteCapsule(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {}
}

function handlePageChange(page) {
  currentPage.value = page
  loadData()
}
</script>

<template>
  <el-card>
    <template #header>
      <div style="display: flex; justify-content: space-between; align-items: center;">
        <h3>我的时光胶囊</h3>
        <el-button type="primary" @click="router.push('/capsule/create')">创建新胶囊</el-button>
      </div>
    </template>

    <el-table :data="capsules" style="width: 100%;">
      <el-table-column prop="title" label="标题" min-width="200" />
      <el-table-column prop="visibility" label="可见性" width="100">
        <template #default="{ row }">
          <el-tag :type="row.visibility === 'PUBLIC' ? 'success' : 'info'" size="small">
            {{ row.visibility === 'PUBLIC' ? '公开' : '私密' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="openTime" label="解封时间" width="180" />
      <el-table-column prop="viewCount" label="浏览" width="80" />
      <el-table-column prop="likeCount" label="点赞" width="80" />
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="router.push(`/capsule/detail/${row.id}`)">查看</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="capsules.length === 0" description="还没有创建时光胶囊" />

    <div style="display: flex; justify-content: center; margin-top: 20px;">
      <el-pagination
        v-if="total > 0"
        :current-page="currentPage"
        :page-size="10"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </el-card>
</template>
