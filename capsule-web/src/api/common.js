import request from '@/utils/request'

// 获取分类列表
export function getCategoryList() {
  return request({ url: '/category/list', method: 'get' })
}

// 获取公告列表
export function getNoticeList(params) {
  return request({ url: '/notice/list', method: 'get', params })
}

// 文件上传
export function uploadFile(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/common/upload',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
