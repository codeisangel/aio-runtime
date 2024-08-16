import request from '@/utils/request'


export function getRunTimeVersionApi() {
  return request({
    url: '/runtime/aio/version',
    method: 'get'
  })
}


export function getRunTimeWorkspaceApi() {
  return request({
    url: '/runtime/aio/workspace',
    method: 'get'
  })
}

export function getSystemStartingTimeApi() {
  return request({
    url: '/runtime/aio/system/starting/time',
    method: 'get'
  })
}

export function getSystemStartedTimeApi() {
  return request({
    url: '/runtime/aio/system/started/time',
    method: 'get'
  })
}

export function getHostInfoApi() {
  return request({
    url: '/runtime/aio/system/hostInfo',
    method: 'get'
  })
}

export function getPIDApi() {
  return request({
    url: '/runtime/aio/system/pid',
    method: 'get'
  })
}
export function getOSApi() {
  return request({
    url: '/runtime/aio/system/os',
    method: 'get'
  })
}
