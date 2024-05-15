import axios from '@/utils/request'
import request from "@/utils/request";

function add(data) {
  return axios({
    url: 'author/add',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: 'author/del',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function update(data) {
  return axios({
    url: 'author/update',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function fetch(id) {
  return request({
    url: 'author/fetch',
    method: 'get',
    params: { id }
  })
}

function fetchAll() {
  return axios({
    url: 'author/fetchAll',
    method: 'GET',
  })
}

function fetchList(data) {
  return axios({
    url: 'author/fetchList',
    method: 'POST',
    data: {
      ...data
    }
  })
}

export default {add, del, update, fetch, fetchList, fetchAll}