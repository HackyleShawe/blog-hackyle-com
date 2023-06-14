<template>
  <div style="margin: 10px 10px">
    <div>
      <h3>Database Backup</h3>
      <el-input
        placeholder="Input Database Name" v-model="databaseName" minlength="1" maxlength="50" show-word-limit
        length="100px" clearable>
      </el-input>

      <div style="text-align: center; margin: 8px">
        <el-button type="primary" @click="databaseBackup">Start Backup</el-button>
      </div>
    </div>
    <hr/>

    <div>
      <h3>Database Restore</h3>
      <div style="text-align: center">
        <el-upload
          class="upload-demo"
          ref="upload"
          name="restoreSql"
          :action="databaseRestoreUploadURL"
          :on-error="databaseRestoreErrFunc"
          :on-success="databaseRestoreSuccessFunc"
          :auto-upload="false">
          <el-button slot="trigger" type="info">Select SQL File</el-button>
          <el-button style="margin-left: 10px;" type="success" @click="databaseRestore">Start Restore</el-button>
        </el-upload>
      </div>
    </div>
    <hr/>

  </div>
</template>

<script>
import {getToken} from '@/utils/auth'
import systemAPi from '@/api/system/system'

export default {
  name: 'DataBackup',
  data() {
    return {
      databaseName: '',
      databaseRestoreUploadURL: process.env.VUE_APP_BACKEND_API + '/system/databaseRestore?token='+getToken()
    }
  },
  methods: {
    databaseBackup() {
      if(this.databaseName === '' || this.databaseName === null) {
        this.$message.error('Type Database Name, Please!');
        return
      }
      //基于axios的文件下载：https://www.jb51.net/article/257569.htm
      systemAPi.databaseBackup(this.databaseName).then(res => {
          console.log("调用后端进行数据库备份:", res)
          let filename = res.headers.filename
          let blob = new Blob([res.data]);
          let url = window.URL.createObjectURL(blob); // 创建 url 并指向 blob
          let a = document.createElement('a');
          a.href = url;
          a.download = filename;
          a.click();
          window.URL.revokeObjectURL(url); // 释放该 url
        }
      ).catch(err => {
        console.log("调用后端进行数据库备份出现异常：", err)
      })
    },
    databaseRestore() {
      this.$refs.upload.submit()
    },
    databaseRestoreErrFunc(err) {
      console.log("数据库恢复文件上传失败：", err)
      this.$message({
        showClose: true,
        message: err.message,
        type: 'error'
      });
    },
    databaseRestoreSuccessFunc(response) {
      console.log("数据库恢复上传文件成功：", response)
      this.$message({
        showClose: true,
        message: response.message,
      });
    }
  }
}

</script>

