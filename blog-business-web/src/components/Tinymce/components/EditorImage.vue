<!--
支持多文件上传
-->
<template>
  <div class="upload-container">
    <el-button :style="{background:color,borderColor:color}" icon="el-icon-upload" size="mini" type="primary" @click=" dialogVisible=true">
      upload
    </el-button>
    <el-dialog :visible.sync="dialogVisible">
      <!--
        file-list：上传的文件列表, 例如: [{name: 'food.jpg', url: 'https://xxx.cdn.com/xxx.jpg'}]
      -->
      <el-upload
        :multiple="true"
        :file-list="fileList"
        :show-file-list="true"
        :on-remove="handleRemove"
        :on-success="handleSuccess"
        :before-upload="beforeUpload"
        class="editor-slide-upload"
        :headers="postHeaders"
        :action="postLocation"
        list-type="picture-card"
      >
        <el-button size="small" type="primary">
          Click upload
        </el-button>
      </el-upload>
      <el-button @click="dialogVisible = false">
        Cancel
      </el-button>
      <el-button type="primary" @click="handleSubmit">
        Confirm
      </el-button>
    </el-dialog>
  </div>
</template>

<script>
import {getToken} from "@/utils/auth";

export default {
  name: 'EditorSlideUpload',
  props: {
    color: {
      type: String,
      default: '#1890ff'
    }
  },
  data() {
    return {
      postLocation: process.env.VUE_APP_BACKEND_API + "file/upload",
      dialogVisible: false,
      tmpFileList: [], //暂存文件的上传列表
      //listObj: {}, //文件对象
      fileList: [] //待上传的文件列表
    }
  },
  computed: {
    postHeaders() {
      return {
        'Authorization': getToken(),
      }
    },
  },
  methods: {
    //检查所有图片都是否已上传成功
    checkAllSuccess() {
      //检查每一个对象的hasSuccess值是否为true
      //this.tmpFileList.every(fileObj => fileObj.hasSuccess)
      for (let i = 0; i < this.tmpFileList.length; i++) {
        if(this.tmpFileList[i].hasSuccess === false) {
          return false;
        }
      }
      return true;

      //Object.keys(对象)：获取某个对象的所有属性名称。obj = {'aa':11, 'bb': 22}; Object.keys(obj)=['aa', 'bb']
      //return Object.keys(this.listObj).every(item => this.listObj[item].hasSuccess)
    },

    //Stage3: 向调用者传递已上传的图片地址
    handleSubmit() {
      //const arr = Object.keys(this.listObj).map(v => this.listObj[v])
      if (!this.checkAllSuccess()) {
        this.$message('Please wait for all images to be uploaded successfully. If there is a network problem, please refresh the page and upload again!')
        return
      }

      //收集图片的URL
      const fileUrlArr = this.tmpFileList.map(fileObj => fileObj.url)
      //传递数据给调用者
      this.$emit('successCBK', fileUrlArr)
      this.fileList = []
      this.dialogVisible = false
    },

    //Stage2：上传成功后的处理函数
    handleSuccess(response, file) {
      const uid = file.uid
      for (let i = 0; i < this.tmpFileList.length; i++) {
        if(uid === this.tmpFileList[i].uid) {
          this.tmpFileList[i].url = response.data[0].url
          this.tmpFileList[i].hasSuccess = true
          return
        }
      }

      //const objKeyArr = Object.keys(this.listObj)
      //for (let i = 0, len = objKeyArr.length; i < len; i++) {
      //  if (this.listObj[objKeyArr[i]].uid === uid) {
      //    this.listObj[objKeyArr[i]].url = response.data[i].url
      //    this.listObj[objKeyArr[i]].hasSuccess = true
      //    return
      //  }
      //}
    },
    handleRemove(file) {
      const uid = file.uid
      for (let i = 0; i < this.tmpFileList.length; i++) {
        if(uid === this.tmpFileList[i]) {
          delete this.tmpFileList[i]
          return
        }
      }

      //const objKeyArr = Object.keys(this.listObj)
      //for (let i = 0, len = objKeyArr.length; i < len; i++) {
      //  if (this.listObj[objKeyArr[i]].uid === uid) {
      //    delete this.listObj[objKeyArr[i]]
      //    return
      //  }
      //}
    },

    //Stage 1: 在文件上传前，把文件信息放到tmpFileList中
    beforeUpload(file) {
      const _self = this
      const _URL = window.URL || window.webkitURL
      //const fileName = file.uid

      //初始化文件的信息
      let fileObj = { hasSuccess: false, uid: file.uid }
      _self.tmpFileList.push(fileObj) //放到List中，便于后续检查是否上传成功

      //this.listObj[fileName] = {}
      return new Promise((resolve) => {
        const img = new Image()
        img.src = _URL.createObjectURL(file)
        //img.onload = function() {
          //初始化文件的信息
          //_self.fileObj = { hasSuccess: false, uid: file.uid, width: this.width, height: this.height }
          //_self.tmpFileList.push(fileObj) //放到List中，便于后续检查是否上传成功
        //}
        resolve(true)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.editor-slide-upload {
  margin-bottom: 20px;
  ::v-deep .el-upload--picture-card {
    width: 100%;
  }
}
</style>
