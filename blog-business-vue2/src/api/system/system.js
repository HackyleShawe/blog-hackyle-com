import request from "@/utils/request";

function systemStatus() {
  return request({
    url: '/system/systemStatus',
    method: 'get',
  })
}

function databaseBackup(databaseName) {
  return request({
    url: '/system/databaseBackup',
    method: 'get',
    params: { "databaseName":databaseName },
    responseType: 'blob' //axios支持文件下载的关键
  })
}


export default {systemStatus, databaseBackup}

