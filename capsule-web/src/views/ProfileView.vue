<script setup>
import { ref, computed, onMounted } from 'vue'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'
import { uploadFile } from '@/api/common'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const BASE_URL = 'https://campus-time-capsule.onrender.com'
const userStore = useUserStore()
const userForm = ref({
  nickname: '',
  email: '',
  phone: '',
  avatar: ''
})
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const activeTab = ref('info')

// 计算完整头像URL用于展示
const fullAvatarUrl = computed(() => {
  const avatar = userForm.value.avatar
  if (!avatar) return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
  return avatar.startsWith('http') ? avatar : BASE_URL + avatar
})

onMounted(async () => {
  try {
    const res = await getUserInfo()
    userForm.value = {
      nickname: res.data.nickname || '',
      email: res.data.email || '',
      phone: res.data.phone || '',
      avatar: res.data.avatar || ''
    }
  } catch (e) {}
})

// 头像上传
async function handleAvatarUpload(uploadEvent) {
  const file = uploadEvent.file
  try {
    const res = await uploadFile(file)
    userForm.value.avatar = res.data
    ElMessage.success('头像上传成功，请点击保存修改')
  } catch (e) {
    ElMessage.error('头像上传失败')
  }
}

async function handleUpdateInfo() {
  try {
    await updateUserInfo(userForm.value)
    userStore.updateNickname(userForm.value.nickname)
    userStore.updateAvatar(userForm.value.avatar)
    ElMessage.success('个人信息修改成功')
  } catch (e) {}
}

async function handleUpdatePassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.newPassword) {
    ElMessage.warning('请填写完整密码信息')
    return
  }
  if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    ElMessage.warning('两次输入的新密码不一致')
    return
  }
  try {
    await updatePassword({
      oldPassword: passwordForm.value.oldPassword,
      newPassword: passwordForm.value.newPassword
    })
    ElMessage.success('密码修改成功')
    passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
  } catch (e) {}
}
</script>

<template>
  <el-card>
    <template #header>
      <h3>个人中心</h3>
    </template>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="info">
        <el-form :model="userForm" label-width="100px" style="max-width: 500px;">
          <el-form-item label="头像">
            <div style="display: flex; align-items: center; gap: 16px;">
              <el-avatar :size="64" :src="fullAvatarUrl" />
              <el-upload
                :show-file-list="false"
                :http-request="handleAvatarUpload"
                accept="image/*"
              >
                <el-button size="small" type="primary">更换头像</el-button>
              </el-upload>
            </div>
          </el-form-item>
          <el-form-item label="昵称">
            <el-input v-model="userForm.nickname" placeholder="请输入昵称" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="userForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="userForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUpdateInfo">保存修改</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane label="修改密码" name="password">
        <el-form :model="passwordForm" label-width="100px" style="max-width: 500px;">
          <el-form-item label="原密码">
            <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码" show-password />
          </el-form-item>
          <el-form-item label="新密码">
            <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码" show-password />
          </el-form-item>
          <el-form-item label="确认密码">
            <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>
