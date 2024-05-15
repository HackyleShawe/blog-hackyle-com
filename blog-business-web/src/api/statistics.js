import request from "@/utils/request";

function countNumber() {
  return request({
    url: 'statistics/countNumber',
    method: 'get',
  })
}

export default {countNumber}
