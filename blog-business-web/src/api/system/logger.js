import axios from "@/utils/request";

function articleAccessLog(data) {
  return axios({
    url: '/logger/articleAccess',
    method: 'POST',
    data: {
      ...data
    }
  })
}
export default {articleAccessLog, }
