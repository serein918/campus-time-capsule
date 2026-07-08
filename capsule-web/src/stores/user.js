import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value.role === 'ADMIN')

  function setLoginInfo(data) {
    token.value = data.token
    userInfo.value = {
      userId: data.userId,
      nickname: data.nickname,
      avatar: data.avatar,
      role: data.role
    }
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  function logout() {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  function updateNickname(nickname) {
    userInfo.value.nickname = nickname
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  return { token, userInfo, isLoggedIn, isAdmin, setLoginInfo, logout, updateNickname }
})
