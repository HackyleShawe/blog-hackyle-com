import axios from '@/utils/request'
import request from "@/utils/request";

function add(data) {
  return axios({
    url: 'category/add',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: 'category/del',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function update(data) {
  return axios({
    url: 'category/update',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function fetch(id) {
  return request({
    url: 'category/fetch',
    method: 'get',
    params: { id }
  })
}

function fetchAll() {
  return axios({
    url: 'category/fetchAll',
    method: 'GET',
  })
}

function fetchList(queryInfo) {
  return axios({
    url: 'category/fetchList',
    method: 'POST',
    data: {
      ...queryInfo
    }
  })
}

export default {add, del, update, fetch, fetchList, fetchAll}
