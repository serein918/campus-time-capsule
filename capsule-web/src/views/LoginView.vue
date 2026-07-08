<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { login } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginForm = ref({
  username: '',
  password: ''
})
const loading = ref(false)

async function handleLogin() {
  if (!loginForm.value.username || !loginForm.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(loginForm.value)
    userStore.setLoginInfo(res.data)
    ElMessage.success('登录成功')
    router.push('/home')
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="display: flex; justify-content: center; align-items: center; min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
    <el-card style="width: 400px; border-radius: 12px;">
      <h2 style="text-align: center; margin-bottom: 30px; color: #409eff;">校园时光胶囊</h2>
      <el-form :model="loginForm" label-width="0">
        <el-form-item>
          <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%;" size="large" :loading="loading" @click="handleLogin">登 录</el-button>
        </el-form-item>
      </el-form>
      <div style="text-align: center;">
        <span style="color: #999;">还没有账号？</span>
        <el-link type="primary" @click="router.push('/register')">立即注册</el-link>
      </div>
    </el-card>
  </div>
</template>
