import request from '@/utils/request'

function add(data) {
  return request({
    url: '/article/add',
    method: 'post',
    data
  })
}

function del(ids) {
  return request({
    url: '/article/del',
    method: 'delete',
    params: {ids}
  })
}

function delReal(ids) {
  return request({
    url: '/article/delReal',
    method: 'delete',
    params: {ids}
  })
}

function update(data) {
  return request({
    url: 'article/update',
    method: 'put',
    data
  })
}

function fetchList(data) {
  return request({
    url: '/article/fetchList',
    method: 'post',
    data
  })
}

function fetch(id) {
  return request({
    url: '/article/fetch',
    method: 'get',
    params: { id }
  })
}

export default {add, del, delReal, update, fetchList, fetch}
