import axios from '@/utils/request'

function add(data) {
  return axios({
    url: 'friendLink/add',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: 'friendLink/del',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function update(data) {
  return axios({
    url: 'friendLink/update',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function fetchList(data) {
  return axios({
    url: 'friendLink/fetchList',
    method: 'POST',
    data: {
      ...data
    }
  })
}


export default {add, del, update, fetchList}
