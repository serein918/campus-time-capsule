<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCapsuleDetail } from '@/api/capsule'
import { getComments, addComment } from '@/api/comment'
import { toggleLike, getLikeStatus } from '@/api/interaction'
import { toggleFavorite, getFavoriteStatus } from '@/api/interaction'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const capsule = ref(null)
const comments = ref([])
const commentContent = ref('')
const liked = ref(false)
const favorited = ref(false)
const loading = ref(true)

const capsuleId = route.params.id

onMounted(async () => {
  try {
    const res = await getCapsuleDetail(capsuleId)
    capsule.value = res.data
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }

  // 加载评论
  try {
    const commentRes = await getComments(capsuleId, { page: 1, size: 50 })
    comments.value = commentRes.data.records || []
  } catch (e) {}

  // 加载点赞和收藏状态
  if (userStore.isLoggedIn) {
    try {
      const likeRes = await getLikeStatus(capsuleId)
      liked.value = likeRes.data
    } catch (e) {}
    try {
      const favRes = await getFavoriteStatus(capsuleId)
      favorited.value = favRes.data
    } catch (e) {}
  }
})

async function handleLike() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await toggleLike(capsuleId)
    liked.value = res.data.liked
    // 更新本地计数
    if (capsule.value) {
      capsule.value.likeCount += liked.value ? 1 : -1
    }
  } catch (e) {}
}

async function handleFavorite() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    const res = await toggleFavorite(capsuleId)
    favorited.value = res.data.favorited
    if (capsule.value) {
      capsule.value.favoriteCount += favorited.value ? 1 : -1
    }
  } catch (e) {}
}

async function handleComment() {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  try {
    await addComment({ capsuleId: Number(capsuleId), content: commentContent.value })
    ElMessage.success('评论成功')
    commentContent.value = ''
    // 重新加载评论
    const commentRes = await getComments(capsuleId, { page: 1, size: 50 })
    comments.value = commentRes.data.records || []
  } catch (e) {}
}
</script>

<template>
  <div v-loading="loading">
    <el-card v-if="capsule">
      <h2 style="margin-bottom: 16px;">{{ capsule.title }}</h2>
      <div style="color: #999; font-size: 14px; margin-bottom: 20px;">
        <span>创建时间: {{ capsule.createTime }}</span>
        <span style="margin-left: 20px;">浏览: {{ capsule.viewCount }}</span>
        <span style="margin-left: 20px;">点赞: {{ capsule.likeCount }}</span>
        <span style="margin-left: 20px;">收藏: {{ capsule.favoriteCount }}</span>
      </div>
      <el-divider />
      <p v-if="capsule.summary" style="color: #666; font-style: italic; margin-bottom: 16px;">{{ capsule.summary }}</p>
      <div style="line-height: 1.8; white-space: pre-wrap;">{{ capsule.content }}</div>
      <el-divider />
      <div style="display: flex; gap: 16px;">
        <el-button :type="liked ? 'danger' : 'default'" @click="handleLike">
          {{ liked ? '已点赞' : '点赞' }} {{ capsule.likeCount }}
        </el-button>
        <el-button :type="favorited ? 'warning' : 'default'" @click="handleFavorite">
          {{ favorited ? '已收藏' : '收藏' }} {{ capsule.favoriteCount }}
        </el-button>
      </div>
    </el-card>

    <!-- 评论区 -->
    <el-card style="margin-top: 20px;">
      <template #header>
        <span style="font-weight: bold;">评论区 ({{ comments.length }})</span>
      </template>
      <div style="margin-bottom: 20px;">
        <el-input v-model="commentContent" type="textarea" :rows="3" placeholder="写下你的评论..." />
        <el-button type="primary" style="margin-top: 10px;" @click="handleComment">发表评论</el-button>
      </div>
      <el-divider />
      <div v-for="comment in comments" :key="comment.id" style="padding: 12px 0; border-bottom: 1px solid #f0f0f0;">
        <p>{{ comment.content }}</p>
        <span style="color: #999; font-size: 12px;">{{ comment.createTime }}</span>
      </div>
      <el-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧" />
    </el-card>
  </div>
</template>
