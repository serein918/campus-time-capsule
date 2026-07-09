<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSquareCapsules } from '@/api/capsule'
import { getCategoryList } from '@/api/common'

const BASE_URL = 'https://campus-time-capsule.onrender.com'
const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const router = useRouter()
const capsules = ref([])
const categories = ref([])
const currentPage = ref(1)
const total = ref(0)
const keyword = ref('')
const categoryId = ref(null)

function getAvatarUrl(avatar) {
  if (!avatar) return DEFAULT_AVATAR
  return avatar.startsWith('http') ? avatar : BASE_URL + avatar
}

async function loadData() {
  try {
    const res = await getSquareCapsules({
      page: currentPage.value,
      size: 12,
      keyword: keyword.value || undefined,
      categoryId: categoryId.value || undefined
    })
    capsules.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {}
}

onMounted(async () => {
  try {
    const catRes = await getCategoryList()
    categories.value = catRes.data || []
  } catch (e) {}
  loadData()
})

function handleSearch() {
  currentPage.value = 1
  loadData()
}

function handleCategoryChange(id) {
  categoryId.value = id
  currentPage.value = 1
  loadData()
}

function handlePageChange(page) {
  currentPage.value = page
  loadData()
}
</script>

<template>
  <div>
    <el-card style="margin-bottom: 20px;">
      <div style="display: flex; align-items: center; gap: 16px; flex-wrap: wrap;">
        <el-input v-model="keyword" placeholder="搜索胶囊标题或摘要..." style="width: 300px;" clearable @clear="handleSearch">
          <template #append>
            <el-button @click="handleSearch">搜索</el-button>
          </template>
        </el-input>
        <el-radio-group v-model="categoryId" @change="handleCategoryChange">
          <el-radio-button :value="null">全部</el-radio-button>
          <el-radio-button v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.name }}</el-radio-button>
        </el-radio-group>
      </div>
    </el-card>

    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="capsule in capsules" :key="capsule.id">
        <el-card shadow="hover" style="margin-bottom: 16px; cursor: pointer;" @click="router.push(`/capsule/detail/${capsule.id}`)">
          <h4 style="margin-bottom: 8px;">{{ capsule.title }}</h4>
          <p style="color: #666; font-size: 14px; height: 40px; overflow: hidden;">{{ capsule.summary || '暂无摘要' }}</p>
          <div style="display: flex; align-items: center; gap: 8px; margin-top: 10px; margin-bottom: 8px;">
            <el-avatar :size="24" :src="getAvatarUrl(capsule.ownerAvatar)" />
            <span style="font-size: 13px; color: #555;">{{ capsule.ownerNickname || '匿名用户' }}</span>
          </div>
          <div style="display: flex; justify-content: space-between; margin-top: 8px; color: #999; font-size: 12px;">
            <span>浏览 {{ capsule.viewCount }}</span>
            <span>点赞 {{ capsule.likeCount }}</span>
            <span>收藏 {{ capsule.favoriteCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="capsules.length === 0" description="暂无公开胶囊" />

    <div style="display: flex; justify-content: center; margin-top: 20px;">
      <el-pagination
        v-if="total > 0"
        :current-page="currentPage"
        :page-size="12"
        :total="total"
        layout="prev, pager, next"
        @current-change="handlePageChange"
      />
    </div>
  </div>
</template>
