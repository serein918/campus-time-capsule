import request from '@/utils/request'

// 发表评论
export function addComment(data) {
  return request({ url: '/comment', method: 'post', data })
}

// 删除评论
export function deleteComment(id) {
  return request({ url: `/comment/${id}`, method: 'delete' })
}

// 获取胶囊评论列表
export function getComments(capsuleId, params) {
  return request({ url: `/comment/list/${capsuleId}`, method: 'get', params })
}
