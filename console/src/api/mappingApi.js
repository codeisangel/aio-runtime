import request from '@/utils/request'

export function getMappingPageApi(data) {
  return request({
    url: '/runtime/aio/mapping/page',
    method: 'get',
    params:data
  })
}

