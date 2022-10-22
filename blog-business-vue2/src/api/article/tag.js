import axios from '@/utils/request'
import request from "@/utils/request";

function add(data) {
  return axios({
    url: 'tag/add',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: 'tag/del',
    method: 'DELETE',
    params: {
      ids
    }
  })
}


function update(data) {
  return axios({
    url: 'tag/update',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function fetch(id) {
  return request({
    url: 'tag/fetch',
    method: 'get',
    params: { id }
  })
}

function fetchAll() {
  return axios({
    url: 'tag/fetchAll',
    method: 'GET',
  })
}

function fetchList(data) {
  return axios({
    url: 'tag/fetchList',
    method: 'POST',
    data: {
      ...data
    }
  })
}

export default {add, del, update, fetch, fetchList, fetchAll}