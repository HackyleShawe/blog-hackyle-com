<template>
  <div>

    <!--搜索-->
    <div style="margin-top: 10px">
      <el-form :inline="true" :model="queryInfo" class="demo-form-inline">
        <el-form-item label="Name">
          <el-input v-model="queryInfo.name" placeholder="Type Name"></el-input>
        </el-form-item>
        <el-form-item label="Email">
          <el-input v-model="queryInfo.email" placeholder="Type Link Email"></el-input>
        </el-form-item>
        <el-form-item label="Phone">
          <el-input v-model="queryInfo.phone" placeholder="Type Phone"></el-input>
        </el-form-item>
        <el-form-item label="Link">
          <el-input v-model="queryInfo.link" placeholder="Type Link"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="search">Query</el-button>
        </el-form-item>

        <br/>
        <el-form-item label="Content">
          <el-input v-model="queryInfo.content" placeholder="Type Content"></el-input>
        </el-form-item>
        <el-form-item label="Deleted">
          <el-radio v-model="queryInfo.deleted" :label="true">YES</el-radio>
          <el-radio v-model="queryInfo.deleted" :label="false">NO</el-radio>
          <el-radio v-model="queryInfo.deleted" :label="undefined">Unknown</el-radio>
        </el-form-item>
      </el-form>
    </div>

    <!--反馈留言展示列表-->
    <el-table :data="feedbackMessageList" row-key="id" :tree-props="{children: 'replyComments'}">
      <el-table-column label="ID" prop="id" width="50" show-overflow-tooltip></el-table-column>
      <el-table-column label="Name" prop="name" width="80" show-overflow-tooltip></el-table-column>
      <el-table-column label="Email" prop="email" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="Phone" prop="phone" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="Link" prop="link" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="IP" prop="ip" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="Content" prop="content" show-overflow-tooltip width="400"></el-table-column>

      <el-table-column label="Time" width="115">
        <template v-slot="scope">{{ scope.row.updateTime }}</template>
      </el-table-column>
      <el-table-column label="IsDeleted" width="85">
        <template v-slot="scope">
          <el-switch v-model="scope.row.deleted" @change="deletedChanged(scope.row)"></el-switch>
        </template>
      </el-table-column>

    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
                   :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>
  </div>
</template>

<script>
import feedbackMessageApi from '@/api/content/feedback-message'

export default {
  name: "FeedbackMessage",
  data() {
    return {
      queryInfo: { //查询搜索条件
        pageNum: 1,
        pageSize: 10,
        name: '',
        email: '',
        phone: '',
        link: '',
        content: '',
        deleted: undefined, //是否删除
        released: undefined, //是否已经审查发布
      },
      total: 0,
      feedbackMessageList: [],
      //editDialogVisible: false,
    }
  },
  created() {
    this.getFeedbackMessageList()
  },
  methods: {
    getFeedbackMessageList() {
      let param = {
        "data": {
          "currentPage": this.queryInfo.pageNum,
          "pageSize": this.queryInfo.pageSize,
          "condition": {
            "name": this.queryInfo.name,
            "email": this.queryInfo.email,
            "phone": this.queryInfo.phone,
            "link": this.queryInfo.link,
            "content": this.queryInfo.content,
            "deleted": this.queryInfo.deleted,
            "released": this.queryInfo.released,
          }
        }
      }
      feedbackMessageApi.fetchList(param).then(res => {
        this.feedbackMessageList = res.data.rows
        this.total = res.data.total
      })
    },

    search() {
      let param = {
        "data": {
          "currentPage": this.queryInfo.pageNum,
          "pageSize": this.queryInfo.pageSize,
          "condition": {
            "name": this.queryInfo.name,
            "email": this.queryInfo.email,
            "phone": this.queryInfo.phone,
            "link": this.queryInfo.link,
            "content": this.queryInfo.content,
            "deleted": this.queryInfo.deleted,
            "released": this.queryInfo.released,
          }
        }
      }
      feedbackMessageApi.fetchList(param).then(res => {
        this.feedbackMessageList = res.data.rows
        this.total = res.data.total
      })
    },

    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getFeedbackMessageList()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getFeedbackMessageList()
    },

    //切换反馈留言公开状态（如果切换成隐藏，则该反馈留言的所有子反馈留言都修改为同样的隐藏状态）
    deletedChanged(row) {
      let param = {
        "data": {
          "id": row.id,
          "deleted": row.deleted,
        }
      }
      feedbackMessageApi.update(param).then(res => {
        this.$message.success(res.message)
        this.getFeedbackMessageList() //成功后再获取新的数据
      })
    },
  }
}
</script>
