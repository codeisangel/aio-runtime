import request from '@/utils/request'

export function searchDocumentApi(data) {
  return request({
    url: '/document/data/manage/table/list',
    method: 'get',
    data:data
  })
}

export function releaseMenuRelationshipApi(documentId) {
  return request({
    url: '/document/release/menu/relationship',
    method: 'put',
    params:{id:documentId}
  })
}

