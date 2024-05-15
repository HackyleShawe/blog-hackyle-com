import axios from '@/utils/request'

function add(data) {
  return axios({
    url: 'config/add',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: 'config/del',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function update(data) {
  return axios({
    url: 'config/update',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function fetch(id) {
  return axios({
    url: 'config/fetch',
    method: 'get',
    params: { id }
  })
}

function fetchList(data) {
  return axios({
    url: 'config/fetchList',
    method: 'POST',
    data: {
      ...data
    }
  })
}

export default {add, del, update, fetch, fetchList}
