import request from '@/utils/request'

export function getErrorReportedPageApi(data,params) {
  return request({
    url: '/runtime/aio/error/reported/page',
    method: 'post',
    params:params,
    data:data
  })
}

