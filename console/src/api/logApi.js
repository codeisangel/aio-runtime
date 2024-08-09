import request from '@/utils/request'

export function getLogPageApi(data,page) {
  return request({
    url: '/runtime/aio/log/page',
    method: 'post',
    data,
    params:page
  })
}

export function getLogSchemeOptionsApi(data,page) {
  return request({
    url: '/runtime/aio/log/scheme/options',
    method: 'get',
    params:data
  })
}

export function getLogSchemeDetailsApi(id) {
  return request({
    url: '/runtime/aio/log/scheme',
    method: 'get',
    params:{id:id}
  })
}

export function getLogLevelListApi(data) {
  return request({
    url: '/runtime/aio/log/level/list',
    method: 'get',
    params:data
  })
}

export function updateLogLevelApi(data) {
  return request({
    url: '/runtime/aio/log/level',
    method: 'put',
    data
  })
}


export function saveLogSchemeApi(data) {
  return request({
    url: '/runtime/aio/log/scheme',
    method: 'post',
    data
  })
}
