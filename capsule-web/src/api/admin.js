import request from '@/utils/request'

// 获取用户列表
export function getUserList(params) {
  return request({ url: '/admin/user/list', method: 'get', params })
}

// 禁用/启用用户
export function updateUserStatus(userId, status) {
  return request({ url: `/admin/user/status/${userId}`, method: 'put', params: { status } })
}

// 获取胶囊列表（管理员）
export function getCapsuleList(params) {
  return request({ url: '/admin/capsule/list', method: 'get', params })
}

// 删除胶囊（管理员）
export function deleteCapsule(id) {
  return request({ url: `/admin/capsule/${id}`, method: 'delete' })
}

// 删除评论（管理员）
export function deleteComment(id) {
  return request({ url: `/admin/comment/${id}`, method: 'delete' })
}

// 发布公告
export function addNotice(data) {
  return request({ url: '/admin/notice', method: 'post', data })
}

// 修改公告
export function updateNotice(data) {
  return request({ url: '/admin/notice', method: 'put', data })
}

// 删除公告
export function deleteNotice(id) {
  return request({ url: `/admin/notice/${id}`, method: 'delete' })
}

// 添加分类
export function addCategory(data) {
  return request({ url: '/admin/category', method: 'post', data })
}

// 删除分类
export function deleteCategory(id) {
  return request({ url: `/admin/category/${id}`, method: 'delete' })
}

// 获取统计数据
export function getStatistics() {
  return request({ url: '/admin/statistics', method: 'get' })
}
