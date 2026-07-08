<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { register } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

const registerForm = ref({
  username: '',
  password: '',
  confirmPassword: '',
  nickname: '',
  email: ''
})
const loading = ref(false)

async function handleRegister() {
  if (!registerForm.value.username || !registerForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  if (registerForm.value.password !== registerForm.value.confirmPassword) {
    ElMessage.warning('两次输入的密码不一致')
    return
  }
  loading.value = true
  try {
    await register({
      username: registerForm.value.username,
      password: registerForm.value.password,
      nickname: registerForm.value.nickname,
      email: registerForm.value.email
    })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="display: flex; justify-content: center; align-items: center; min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <el-card style="width: 420px; border-radius: 12px;">
      <h2 style="text-align: center; margin-bottom: 30px; color: #409eff;">用户注册</h2>
      <el-form :model="registerForm" label-width="0">
        <el-form-item>
          <el-input v-model="registerForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.nickname" placeholder="请输入昵称（选填）" prefix-icon="UserFilled" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.email" placeholder="请输入邮箱（选填）" prefix-icon="Message" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请确认密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%;" size="large" :loading="loading" @click="handleRegister">注 册</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center;">
        <span style="color: #999;">已有账号？</span>
        <el-link type="primary" @click="router.push('/login')">去登录</el-link>
      </div>
    </el-card>
  </div>
</template>
