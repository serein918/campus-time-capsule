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

// 💡 记录父评论ID与动态提示文本
const parentId = ref(null)
const placeholderText = ref('写下你的评论...')

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

// 💡点击评论旁的“回复”按钮时触发
function clickReply(comment) {
  parentId.value = comment.id 
  placeholderText.value = `回复 @${comment.nickname || '用户'}：` // 动态修改输入框提示
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
    await addComment({ 
      capsuleId: Number(capsuleId), 
      content: commentContent.value,
      parentId: parentId.value 
    })
    ElMessage.success('评论成功')
    
    // 💡 发表成功后，重置所有的状态
    commentContent.value = ''
    parentId.value = null
    placeholderText.value = '写下你的评论...'
    
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

    <el-card style="margin-top: 20px;">
      <template #header>
        <span style="font-weight: bold;">评论区 ({{ comments.length }})</span>
      </template>
      <div style="margin-bottom: 20px;">
        <el-input v-model="commentContent" type="textarea" :rows="3" :placeholder="placeholderText" />
        <div style="display: flex; gap: 12px; align-items: center; margin-top: 10px;">
          <el-button type="primary" @click="handleComment">发表评论</el-button>
          <el-button v-if="parentId" size="small" @click="() => { parentId = null; placeholderText = '写下你的评论...' }">取消回复</el-button>
        </div>
      </div>
      <el-divider />
      
      <div v-for="comment in comments" :key="comment.id" style="padding: 14px 0; border-bottom: 1px solid #f0f0f0;">
        <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 6px;">
          <el-avatar :size="28" :src="comment.avatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'" />
          <span style="font-weight: bold; font-size: 14px; color: #333;">{{ comment.nickname || '神秘用户' }}</span>
        </div>
        
        <p style="margin-left: 36px; color: #353535; font-size: 14px; line-height: 1.6;">
          <span v-if="comment.parentNickname" style="color: #409EFF; font-weight: 500; margin-right: 4px;">
            回复 @{{ comment.parentNickname }}：
          </span>
          {{ comment.content }}
        </p>
        
        <div style="margin-left: 36px; margin-top: 6px; display: flex; align-items: center; gap: 16px;">
          <span style="color: #999; font-size: 12px;">{{ comment.createTime }}</span>
          <span @click="clickReply(comment)" style="color: #409EFF; font-size: 12px; cursor: pointer; user-select: none;">回复</span>
        </div>
      </div>
      
      <el-empty v-if="comments.length === 0" description="暂无评论，快来抢沙发吧" />
    </el-card>
  </div>
</template>