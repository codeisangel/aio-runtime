import request from '@/utils/request'

export function addArticle(data) {
  return request({
    url: '/article',
    method: 'post',
    data:data
  })
}
export function saveArticle(data) {
  return request({
    url: '/article',
    method: 'put',
    data:data
  })
}

export function addArticleLabel(label) {
  return request({
    url: '/article/label',
    method: 'post',
    data:{articleLabel:label}
  })
}

export function getArticleLabel(label) {
  return request({
    url: '/article/label/options',
    method: 'get',
    params:{label:label}
  })
}
export function getArticleLabelList(articleId) {
  return request({
    url: '/article/label',
    method: 'get',
    params:{articleId:articleId}
  })
}

export function getHelpArticle(params) {
  return request({
    url: '/article/help/list',
    method: 'post',
    data:params
  })
}
export function getArticleById(articleId) {
  return request({
    url: '/article',
    method: 'get',
    params:{id:articleId}
  })
}
export function getArticlePage(params) {
  return request({
    url: '/article/page',
    method: 'get',
    params:params
  })
}

export function cleanArticleLabel() {
  return request({
    url: '/article/clean/label',
    method: 'delete'
  })
}

