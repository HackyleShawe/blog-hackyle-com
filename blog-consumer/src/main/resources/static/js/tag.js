
//-----------------------------------标签下的文章搜索 START----------------------------------
$("#tagSearchBtn").click(function () {
  let tagCode = document.getElementById("tagCode").innerText
  let uri = "/tag/" +tagCode+ "/1"
  let keys = $("#tagSearchInput").val()
  if(keys.trim().length !== 0) {
    keys = keys.replaceAll(" ", ",") //替换为英文状态下的逗号
    uri += "?tagKeys="+keys
  }

  location.assign(location.protocol + "//" + location.host + uri) //跳转到该页
})
//-----------------------------------标签下的文章搜索 END-----------------------------------


//---------------------------------------------页切换 START---------------------------------------------
$("#tagPagePass").click(function (event) {
  toPage(event.target.value)
})
$("#tagPageNext").click(function (event) {
  toPage(event.target.value)
})

function toPage(pageNum) {
  let tagCode = document.getElementById("tagCode").innerText

  let uri = "/tag/"+tagCode+"/"+pageNum
  let keys = $("#tagSearchInput").val()
  if(keys.trim().length !== 0) {
    keys = keys.replaceAll(" ", ",") //替换为英文状态下的逗号
    uri += "?tagKeys="+keys
  }

  location.assign(location.protocol + "//" + location.host + uri) //跳转到该页
}
//---------------------------------------------页切换 END---------------------------------------------

