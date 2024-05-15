import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/admin/login',
    method: 'post',
    data
  })
}

export function update(data) {
  return request({
    url: '/admin/update',
    method: 'put',
    data
  })
}

export function getInfo(token) {
  return request({
    url: '/admin/info',
    method: 'get',
    params: { token }
  })
}

export function logout() {
  return request({
    url: '/admin/logout',
    method: 'get'
  })
}

export function code() {
  return request({
    url: '/admin/verificationCode',
    method: 'get'
  })
}
