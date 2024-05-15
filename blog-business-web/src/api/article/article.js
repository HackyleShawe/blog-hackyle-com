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

function fetchAuthor(articleId) {
  return request({
    url: '/article/fetchAuthor',
    method: 'get',
    params: { articleId }
  })
}

function fetchCategory(articleId) {
  return request({
    url: '/article/fetchCategory',
    method: 'get',
    params: { articleId }
  })
}

function fetchTag(articleId) {
  return request({
    url: '/article/fetchTag',
    method: 'get',
    params: { articleId }
  })
}

export default {add, del, delReal, update, fetchList, fetch, fetchAuthor, fetchCategory, fetchTag}
