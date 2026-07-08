import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const request = axios.create({
  baseURL: 'https://campus-time-capsule.onrender.com',
  timeout: 10000
})

// 请求拦截器 - 自动携带Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理错误
request.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code === 200) {
      return res
    } else if (res.code === 401) {
      ElMessage.error('登录已过期，请重新登录')
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
      return Promise.reject(new Error(res.message))
    } else {
      ElMessage.error(res.message || '操作失败')
      return Promise.reject(new Error(res.message))
    }
  },
  (error) => {
    ElMessage.error('网络请求异常，请稍后重试')
    return Promise.reject(error)
  }
)

export default request
