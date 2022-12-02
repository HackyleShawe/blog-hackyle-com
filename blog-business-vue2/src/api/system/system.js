import request from "@/utils/request";

function systemStatus() {
  return request({
    url: '/system/systemStatus',
    method: 'get',
  })
}

export default {systemStatus}
