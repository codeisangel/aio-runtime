import request from '@/utils/request'

export function getSubscribeLogPageApi(data) {
  return request({
    url: '/runtime/log/subscribe/page',
    method: 'get',
    params:data
  })
}
export function updateSubscribeStatusApi(data) {
  return request({
    url: '/runtime/log/subscribe/handled',
    method: 'post',
    data
  })
}

