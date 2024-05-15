import axios from '@/utils/request'

function add(data) {
  return axios({
    url: 'feedbackMessage/add',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: 'feedbackMessage/del',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function update(data) {
  return axios({
    url: 'feedbackMessage/update',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function fetchList(data) {
  return axios({
    url: 'feedbackMessage/fetchList',
    method: 'POST',
    data: {
      ...data
    }
  })
}


export default {add, del, update, fetchList}
