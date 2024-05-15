import request from '@/utils/request'

export function getLogPageApi(data) {
  return request({
    url: '/runtime/log/mapping/record/page',
    method: 'get',
    params:data
  })
}

export function getColumnListApi(tableId) {
  return request({
    url: '/runtime/log/mapping/record/page',
    method: 'get',
    params:{tableId:tableId}
  })
}
