import request from '@/utils/request'

export function getErrorCodePageApi(data) {
  return request({
    url: '/runtime/aio/error/code/page',
    method: 'get',
    params:data
  })
}

export function addErrorCodeApi(data) {
  return request({
    url: '/runtime/aio/error/code',
    method: 'post',
    data
  })
}

export function updateErrorCodeApi(data) {
  return request({
    url: '/runtime/aio/error/code',
    method: 'put',
    data
  })
}
export function deleteErrorCodeApi(data) {
  return request({
    url: '/runtime/aio/error/code',
    method: 'delete',
    data
  })
}
export function exportErrorCodeApi(params) {
  return request({
    url: '/runtime/aio/error/code/export',
    method: 'get',
    params
  })
}
