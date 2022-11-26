<template>
  <div>

    <!--搜索-->
    <div style="margin-top: 10px">
      <el-form :inline="true" :model="queryInfo" class="demo-form-inline">
        <el-form-item label="ArticleURI">
          <el-input v-model="queryInfo.articleURI" placeholder="Type Article URI"></el-input>
        </el-form-item>
        <el-form-item label="IP">
          <el-input v-model="queryInfo.ip" placeholder="Type Link IP"></el-input>
        </el-form-item>
        <el-form-item label="Address">
          <el-input v-model="queryInfo.address" placeholder="Type Address"></el-input>
        </el-form-item>
        <el-form-item label="Browser">
          <el-input v-model="queryInfo.browser" placeholder="Type Browser"></el-input>
        </el-form-item>

        <br/>
        <el-form-item label="TimeUse">
          <el-input v-model="queryInfo.timeUse" type="number" value="10" step="5" min="0" max="9999"></el-input>
        </el-form-item>

        <el-form-item label="Time">
          <!--Notice：这里报错：Instead, use a data or computed property based on the prop's value. Prop being mutated: "placement"-->
          <!--原因是：element-ui版本2.15.9的bug-->
          <el-date-picker
            v-model="queryInfo.timePicker"
            type="datetimerange"
            :picker-options="pickerOptions"
            range-separator="TO"
            start-placeholder="StartTime"
            end-placeholder="EndTime"
            align="right">
          </el-date-picker>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="search">Query</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!--反馈留言展示列表-->
    <el-table :data="feedbackMessageList" row-key="id" :tree-props="{children: 'replyComments'}">
      <!--<el-table-column label="ID" prop="id" width="50" show-overflow-tooltip></el-table-column>-->
      <el-table-column label="No" type="index" width="50"></el-table-column>
      <el-table-column label="ArticleURL" prop="articleUri" width="250" show-overflow-tooltip></el-table-column>
      <el-table-column label="IP" prop="ip" width="150" show-overflow-tooltip></el-table-column>
      <el-table-column label="Address" prop="address" width="150" show-overflow-tooltip></el-table-column>
      <el-table-column label="Browser" prop="browser" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="TimeUse (ms)" prop="timeUse" width="120" show-overflow-tooltip></el-table-column>

      <!--<el-table-column label="Content" prop="content" show-overflow-tooltip width="400"></el-table-column>-->

      <el-table-column label="Time" width="150" show-overflow-tooltip>
        <template v-slot="scope">{{ scope.row.createTime }}</template>
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

import loggerApi from '@/api/system/logger'

export default {
  name: "ArticleAccessLogger",
  data() {
    return {
      queryInfo: { //查询搜索条件
        pageNum: 1,
        pageSize: 10,
        articleURI: '',
        ip: '',
        address: '',
        browser: '',
        timeUse: undefined,
        timePicker: undefined,
      },
      total: 0,
      feedbackMessageList: [],
      //editDialogVisible: false,
      pickerOptions: {
        shortcuts: [{
          text: 'PastWeek',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: 'PastMonth',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: 'PastThreeMonth',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      },
    }
  },
  created() {
    this.getArticleAccessLogList()
  },
  methods: {
    getArticleAccessLogList() {
      let param = {
        "data": {
          "currentPage": this.queryInfo.pageNum,
          "pageSize": this.queryInfo.pageSize,
          "condition": {
            "articleUri": this.queryInfo.articleURI,
            "ip": this.queryInfo.ip,
            "address": this.queryInfo.address,
            "browser": this.queryInfo.browser,
            "timeUse": this.queryInfo.timeUse,
            "timePicker": this.queryInfo.timePicker,
          }
        }
      }
      loggerApi.articleAccessLog(param).then(res => {
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
            "articleUri": this.queryInfo.articleURI,
            "ip": this.queryInfo.ip,
            "address": this.queryInfo.address,
            "browser": this.queryInfo.browser,
            "timeUse": this.queryInfo.timeUse,
            "timePicker": this.queryInfo.timePicker,
          }
        }
      }
      loggerApi.articleAccessLog(param).then(res => {
        this.feedbackMessageList = res.data.rows
        this.total = res.data.total
      })
    },

    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getArticleAccessLogList()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getArticleAccessLogList()
    },
  }
}
</script>
