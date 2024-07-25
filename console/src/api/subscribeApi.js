import request from '@/utils/request'

export function getSubscribeLogPageApi(data) {
  return request({
    url: '/runtime/log/subscribe/page',
    method: 'get',
    params:data
  })
}

