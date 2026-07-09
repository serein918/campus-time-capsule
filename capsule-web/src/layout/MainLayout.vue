<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<template>
  <el-container style="min-height: 100vh;">
    <!-- 顶部导航 -->
    <el-header style="background: #fff; border-bottom: 1px solid #eee; display: flex; align-items: center; justify-content: space-between;">
      <div style="display: flex; align-items: center;">
        <h2 style="color: #409eff; margin-right: 40px; cursor: pointer;" @click="router.push('/home')">
          校园时光胶囊
        </h2>
        <el-menu mode="horizontal" :router="true" :default-active="$route.path" style="border: none;">
          <el-menu-item index="/home">首页</el-menu-item>
          <el-menu-item index="/square">公开广场</el-menu-item>
          <el-menu-item v-if="userStore.isLoggedIn" index="/capsule/create">创建胶囊</el-menu-item>
          <el-menu-item v-if="userStore.isLoggedIn" index="/my-capsules">我的胶囊</el-menu-item>
          <el-menu-item v-if="userStore.isAdmin" index="/admin">后台管理</el-menu-item>
        </el-menu>
      </div>
      <div>
        <template v-if="userStore.isLoggedIn">
          <el-dropdown>
            <span style="cursor: pointer; color: #606266;">
              {{ userStore.userInfo.nickname || '用户' }}
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="router.push('/profile')">个人中心</el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="router.push('/login')">登录</el-button>
          <el-button @click="router.push('/register')">注册</el-button>
        </template>
      </div>
    </el-header>

    <!-- 主内容区 -->
    <el-main style="padding: 20px;">
      <router-view />
    </el-main>

    <!-- 底部 -->
    <el-footer style="text-align: center; color: #999; font-size: 14px; background: #fff; border-top: 1px solid #eee;">
      校园时光胶囊管理系统 &copy; 2026
    </el-footer>
  </el-container>
</template>
