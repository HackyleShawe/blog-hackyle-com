import request from "@/utils/request";

function systemStatus() {
  return request({
    url: '/system/system-status',
    method: 'get',
  })
}

export default {systemStatus}
