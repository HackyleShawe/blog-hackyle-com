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
    responseType: 'blob', //axios支持文件下载的关键
    timeout: 600000, //10 minutes，因为要涉及文件下载，所以将时间设置得长一点
  })
}

function dirBackup(fileDirPath) {
  return request({
    url: '/system/dirBackup',
    method: 'get',
    params: { "fileDirPath":fileDirPath },
    responseType: 'blob', //axios支持文件下载的关键
    timeout: 600000, //10 minutes，因为要涉及文件下载，所以将时间设置得长一点
  })
}

export default {systemStatus, databaseBackup, dirBackup}

