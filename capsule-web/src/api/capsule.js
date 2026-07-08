import request from '@/utils/request'

// 创建胶囊
export function createCapsule(data) {
  return request({ url: '/capsule', method: 'post', data })
}

// 获取胶囊详情
export function getCapsuleDetail(id) {
  return request({ url: `/capsule/${id}`, method: 'get' })
}

// 修改胶囊
export function updateCapsule(data) {
  return request({ url: '/capsule', method: 'put', data })
}

// 删除胶囊
export function deleteCapsule(id) {
  return request({ url: `/capsule/${id}`, method: 'delete' })
}

// 获取我的胶囊列表
export function getMyCapsules(params) {
  return request({ url: '/capsule/my', method: 'get', params })
}

// 获取公开广场胶囊列表
export function getSquareCapsules(params) {
  return request({ url: '/capsule/square', method: 'get', params })
}
