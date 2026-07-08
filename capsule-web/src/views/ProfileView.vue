<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo, updateUserInfo, updatePassword } from '@/api/user'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const userForm = ref({
  nickname: '',
  email: '',
  phone: ''
})
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const activeTab = ref('info')

onMounted(async () => {
  try {
    const res = await getUserInfo()
    userForm.value = {
      nickname: res.data.nickname || '',
      email: res.data.email || '',
      phone: res.data.phone || ''
    }
  } catch (e) {}
})

async function handleUpdateInfo() {
  try {
    await updateUserInfo(userForm.value)
    userStore.updateNickname(userForm.value.nickname)
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
