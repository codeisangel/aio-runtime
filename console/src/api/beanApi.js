import request from '@/utils/request'

export function getBeanPageApi(data) {
  return request({
    url: '/runtime/aio/bean/page',
    method: 'get',
    params:data
  })
}
export function getMethodListApi(data) {
  return request({
    url: '/runtime/aio/bean/method/list',
    method: 'get',
    params:data
  })
}

export function getMethodParametersApi(data) {
  return request({
    url: '/runtime/aio/bean/method/parameters',
    method: 'get',
    params:data
  })
}

export function runMethodApi(data) {
  return request({
    url: '/runtime/aio/bean/method/run',
    method: 'post',
    data
  })
}

