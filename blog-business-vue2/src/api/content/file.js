import axios from '@/utils/request'

function del(data) {
  return axios({
    url: 'file/del',
    method: 'DELETE',
    data: {
      ...data
    }
  })
}

function update(data) {
  return axios({
    url: 'file/update',
    method: 'PUT',
    data: {
      ...data
    }
  })
}

function download(fileLink) {
  return axios({
    url: 'file/download',
    method: 'GET',
    params: { "fileLink": fileLink },
    responseType: 'blob' //axios支持文件下载的关键
  })
}

function fetchList(data) {
  return axios({
    url: 'file/fetchList',
    method: 'POST',
    data: {
      ...data
    }
  })
}


export default {del, update, download, fetchList}
