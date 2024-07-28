import request from '@/utils/request'

export function getEnvironmentPageApi(data) {
  return request({
    url: '/runtime/aio/environment/page',
    method: 'get',
    params:data
  })
}

