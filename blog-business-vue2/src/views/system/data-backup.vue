<template>
  <div style="margin: 10px 10px">
    <div style="margin: 1px 10px; background-color:#d3d1d1">
      <p>注意：</p>
      <p> >> 考虑到服务器的带宽大小，非紧急情况，不建议使用本页面的备份功能进行数据备份。
        因为下载会很慢，HTTP链接很容易超时造等情况造成下载失败</p>
      <p> >> 可以使用本页面的数据恢复功能，前提是要满足对应的恢复条件（文件格式）</p>
    </div>
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
          ref="upload4DatabaseRestore"
          name="restoreSql"
          :action="databaseRestoreUploadURL"
          :on-error="databaseRestoreErrFunc"
          :on-success="databaseRestoreSuccessFunc"
          :auto-upload="false">
          <el-button slot="trigger" type="info">Select Zip SQL File</el-button>
          <el-button style="margin-left: 10px;" type="success" @click="databaseRestore">Start Restore</el-button>
        </el-upload>
      </div>
    </div>
    <hr/>

    <div>
      <h3>Dir Backup</h3>
      <el-input
        placeholder="Input Absolute Dir Path (example:/home/kyle/data)" v-model="dirAbsolutePath" minlength="1" maxlength="50" show-word-limit
        length="100px" clearable>
      </el-input>

      <div style="text-align: center; margin: 8px">
        <el-button type="primary" @click="dirBackup" round>Start Backup</el-button>
      </div>
    </div>
    <hr/>

    <div>
      <h3>Dir Restore</h3>
      <div style="text-align: center">
        <el-input
          placeholder="Input Absolute Restore Dir Path (example:/home/kyle/dataRestore)" v-model="restoreDir" minlength="1" maxlength="50" show-word-limit
          length="100px" clearable>
        </el-input>
        <el-upload
          class="upload-demo"
          ref="upload4DirRestore"
          name="restoreFileZip"
          :action="dirRestoreUploadURL + '&restoreDir='+restoreDir"
          :on-error="dirRestoreErrFunc"
          :on-success="dirRestoreSuccessFunc"
          :auto-upload="false">
          <el-button slot="trigger" type="info" round>Select Zip File</el-button>
          <el-button style="margin-left: 10px;" type="success" @click="dirRestore" round>Start Restore</el-button>
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
      databaseRestoreUploadURL: process.env.VUE_APP_BACKEND_API + '/system/databaseRestore?token='+getToken(),
      dirAbsolutePath: '',
      dirRestoreUploadURL: process.env.VUE_APP_BACKEND_API + '/system/dirRestore?token='+getToken(),
      restoreDir: '',
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
      this.$refs.upload4DatabaseRestore.submit()
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
    },

    dirBackup() {
      if(this.dirAbsolutePath === '' || this.dirAbsolutePath === null) {
        this.$message.error('Type Img Absolute Path, Please!');
        return
      }
      //基于axios的文件下载：https://www.jb51.net/article/257569.htm
      systemAPi.dirBackup(this.dirAbsolutePath).then(res => {
        //console.log("调用后端进行图片资源备份:", res)

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
        console.log("调用后端进行图片资源备份出现异常：", err)
      })
    },
    dirRestore() {
      this.$refs.upload4DirRestore.submit()
    },
    dirRestoreErrFunc(err) {
      console.log("图片资源恢复文件上传失败：", err)
      this.$message({
        showClose: true,
        message: err.message,
        type: 'error'
      });
    },
    dirRestoreSuccessFunc(response) {
      console.log("图片资源恢复上传文件成功：", response)
      this.$message({
        showClose: true,
        message: response.message,
      });
    },
  }
}

</script>

