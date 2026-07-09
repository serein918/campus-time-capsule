<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { createCapsule } from '@/api/capsule'
import { getCategoryList } from '@/api/common'
import { ElMessage } from 'element-plus'

const router = useRouter()
const categories = ref([])
const loading = ref(false)

const form = ref({
  title: '',
  summary: '',
  content: '',
  categoryId: null,
  visibility: 'PRIVATE',
  openTime: '',
  isAnonymous: 0
})

onMounted(async () => {
  try {
    const res = await getCategoryList()
    categories.value = res.data || []
  } catch (e) {}
})

async function handleSubmit() {
  if (!form.value.title || !form.value.content) {
    ElMessage.warning('请填写标题和内容')
    return
  }
  loading.value = true
  try {
    await createCapsule(form.value)
    ElMessage.success('时光胶囊创建成功！')
    router.push('/my-capsules')
  } catch (e) {
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <el-card>
    <template #header>
      <h3>创建时光胶囊</h3>
    </template>
    <el-form :model="form" label-width="100px" style="max-width: 700px;">
      <el-form-item label="胶囊标题" required>
        <el-input v-model="form.title" placeholder="给你的时光胶囊起个名字" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item label="胶囊摘要">
        <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="简短描述（选填）" maxlength="300" show-word-limit />
      </el-form-item>
      <el-form-item label="胶囊内容" required>
        <el-input v-model="form.content" type="textarea" :rows="8" placeholder="写下你想对未来说的话..." />
      </el-form-item>
      <el-form-item label="分类">
        <el-select v-model="form.categoryId" placeholder="选择分类（选填）" clearable>
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="可见性">
        <el-radio-group v-model="form.visibility">
          <el-radio value="PRIVATE">私密（仅自己可见）</el-radio>
          <el-radio value="PUBLIC">公开（到期后所有人可见）</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="发布身份" v-if="form.visibility === 'PUBLIC'">
        <el-radio-group v-model="form.isAnonymous">
          <el-radio :value="0">实名发布</el-radio>
          <el-radio :value="1">匿名发布</el-radio>
        </el-radio-group>
        <div style="color: #999; font-size: 12px; margin-top: 4px;">匿名发布后，其他用户将无法看到你的身份信息</div>
      </el-form-item>
      <el-form-item label="解封时间">
        <el-date-picker v-model="form.openTime" type="datetime" placeholder="选择解封开启时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%;" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :loading="loading" @click="handleSubmit">投递胶囊</el-button>
        <el-button @click="router.back()">取消</el-button>
      </el-form-item>
    </el-form>
  </el-card>
</template>
