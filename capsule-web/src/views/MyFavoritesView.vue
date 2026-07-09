<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getMyFavorites } from '@/api/interaction'
import { ElMessage } from 'element-plus'

const router = useRouter()
const capsules = ref([])

onMounted(async () => {
  try {
    const res = await getMyFavorites()
    capsules.value = res.data || []
  } catch (e) {}
})
</script>

<template>
  <el-card>
    <template #header>
      <h3>我的收藏</h3>
    </template>

    <el-row :gutter="16">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="capsule in capsules" :key="capsule.id">
        <el-card shadow="hover" style="margin-bottom: 16px; cursor: pointer;" @click="router.push(`/capsule/detail/${capsule.id}`)">
          <h4 style="margin-bottom: 8px;">{{ capsule.title }}</h4>
          <p style="color: #666; font-size: 14px; height: 40px; overflow: hidden;">{{ capsule.summary || '暂无摘要' }}</p>
          <div style="display: flex; justify-content: space-between; margin-top: 12px; color: #999; font-size: 12px;">
            <span>浏览 {{ capsule.viewCount }}</span>
            <span>点赞 {{ capsule.likeCount }}</span>
            <span>收藏 {{ capsule.favoriteCount }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="capsules.length === 0" description="还没有收藏任何胶囊" />
  </el-card>
</template>
