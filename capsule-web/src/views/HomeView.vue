<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getSquareCapsules } from '@/api/capsule'
import { getNoticeList } from '@/api/common'

const BASE_URL = 'https://campus-time-capsule.onrender.com'
const DEFAULT_AVATAR = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
const router = useRouter()
const latestCapsules = ref([])
const notices = ref([])

function getAvatarUrl(avatar) {
  if (!avatar) return DEFAULT_AVATAR
  return avatar.startsWith('http') ? avatar : BASE_URL + avatar
}

onMounted(async () => {
  try {
    const capsuleRes = await getSquareCapsules({ page: 1, size: 6 })
    latestCapsules.value = capsuleRes.data.records || []
  } catch (e) {}
  try {
    const noticeRes = await getNoticeList({ page: 1, size: 3 })
    notices.value = noticeRes.data.records || []
  } catch (e) {}
})
</script>

<template>
  <div>
    <!-- 欢迎横幅 -->
    <el-card style="margin-bottom: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border: none;">
      <div style="text-align: center; color: #fff; padding: 40px 0;">
        <h1 style="font-size: 32px; margin-bottom: 16px;">欢迎来到校园时光胶囊</h1>
        <p style="font-size: 16px; margin-bottom: 24px;">记录当下的目标与梦想，在未来某一天重新开启，感受成长的变化</p>
        <el-button type="warning" size="large" @click="router.push('/capsule/create')">创建我的时光胶囊</el-button>
      </div>
    </el-card>

    <!-- 公告区域 -->
    <el-card v-if="notices.length > 0" style="margin-bottom: 20px;">
      <template #header>
        <span style="font-weight: bold;">系统公告</span>
      </template>
      <div v-for="notice in notices" :key="notice.id" style="margin-bottom: 10px; padding-bottom: 10px; border-bottom: 1px solid #f0f0f0;">
        <strong>{{ notice.title }}</strong>
        <p style="color: #999; font-size: 13px; margin-top: 4px;">{{ notice.content }}</p>
      </div>
    </el-card>

    <!-- 最新公开胶囊 -->
    <el-card>
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span style="font-weight: bold;">最新公开胶囊</span>
          <el-button type="primary" link @click="router.push('/square')">查看更多 &gt;</el-button>
        </div>
      </template>
      <el-row :gutter="16">
        <el-col :xs="24" :sm="12" :md="8" v-for="capsule in latestCapsules" :key="capsule.id">
          <el-card shadow="hover" style="margin-bottom: 16px; cursor: pointer;" @click="router.push(`/capsule/detail/${capsule.id}`)">
            <h4 style="margin-bottom: 8px;">{{ capsule.title }}</h4>
            <p style="color: #666; font-size: 14px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">{{ capsule.summary || '暂无摘要' }}</p>
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
      <el-empty v-if="latestCapsules.length === 0" description="暂无公开胶囊" />
    </el-card>
  </div>
</template>
