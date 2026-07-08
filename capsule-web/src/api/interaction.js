import request from '@/utils/request'

// 点赞/取消点赞
export function toggleLike(capsuleId) {
  return request({ url: `/like/${capsuleId}`, method: 'post' })
}

// 查询是否已点赞
export function getLikeStatus(capsuleId) {
  return request({ url: `/like/status/${capsuleId}`, method: 'get' })
}

// 收藏/取消收藏
export function toggleFavorite(capsuleId) {
  return request({ url: `/favorite/${capsuleId}`, method: 'post' })
}

// 查询是否已收藏
export function getFavoriteStatus(capsuleId) {
  return request({ url: `/favorite/status/${capsuleId}`, method: 'get' })
}

// 我的收藏列表
export function getMyFavorites() {
  return request({ url: '/favorite/my', method: 'get' })
}
