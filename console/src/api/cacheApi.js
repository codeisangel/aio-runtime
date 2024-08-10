import request from '@/utils/request'

export function getAllCacheApi() {
  return request({
    url: '/runtime/aio/cache/all',
    method: 'get',
  })
}

export function getCacheContentApi(data) {
  return request({
    url: '/runtime/aio/cache/content',
    method: 'get',
    params: data
  })
}

export function deleteCacheContentApi(data) {
  return request({
    url: '/runtime/aio/cache/content',
    method: 'delete',
    params: data
  })
}
export function clearCacheContentApi(data) {
  return request({
    url: '/runtime/aio/cache/clear',
    method: 'delete',
    params: data
  })
}
