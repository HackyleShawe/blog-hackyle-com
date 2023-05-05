import axios from '@/utils/request'

function add(data) {
  return axios({
    url: 'comment/add',
    method: 'POST',
    data: {
      ...data
    }
  })
}

function del(ids) {
  return axios({
    url: 'comment/del',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function delReal(ids) {
  return axios({
    url: 'comment/delReal',
    method: 'DELETE',
    params: {
      ids
    }
  })
}

function update(data) {
  return axios({
    url: 'comment/update',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function fetchList(data) {
  return axios({
    url: 'comment/fetchList',
    method: 'POST',
    data: {
      ...data
    }
  })
}

//分页获取层级评论
function fetchListByHierarchy(data) {
  return axios({
    url: 'comment/fetchListByHierarchy',
    method: 'POST',
    data: {
      ...data
    }
  })
}

export default {add, del, delReal, update, fetchList, fetchListByHierarchy}
