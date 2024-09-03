import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/runtime/aio/security/person/login',
    method: 'post',
    data
})
}

export function getInfo(token) {
  return request({
    //url: '/kgo/user',
    url: '/runtime/aio/security/user/login/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/runtime/aio/security/logout',
    method: 'post'
  })
}
