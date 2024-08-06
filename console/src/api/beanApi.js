import request from '@/utils/request'

export function getBeanPageApi(data) {
  return request({
    url: '/runtime/aio/bean/page',
    method: 'get',
    params:data
  })
}

