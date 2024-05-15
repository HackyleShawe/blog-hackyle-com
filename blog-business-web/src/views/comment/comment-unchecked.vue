<!--
  未审查的评论
-->
<template>
  <div>
    <!--搜索-->
    <div style="margin-top: 10px">
      <el-form :inline="true" :model="queryInfo" class="demo-form-inline">
        <el-form-item label="Name">
          <el-input v-model="queryInfo.name" placeholder="Type Name"></el-input>
        </el-form-item>
        <el-form-item label="Email">
          <el-input v-model="queryInfo.email" placeholder="Type Email"></el-input>
        </el-form-item>
        <el-form-item label="Content">
          <el-input v-model="queryInfo.content" placeholder="Type Content"></el-input>
        </el-form-item>
        <el-form-item label="ParentId">
          <el-input v-model="queryInfo.parentId" placeholder="Type ParentId"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="commentSearch">Query</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!--评论展示列表-->
    <el-table :data="commentList" row-key="id" :tree-props="{children: 'replyComments'}">
      <el-table-column label="ID" prop="id" width="130" show-overflow-tooltip></el-table-column>
      <el-table-column label="Name" prop="name" width="80" show-overflow-tooltip></el-table-column>
      <el-table-column label="Email" prop="email" width="150" show-overflow-tooltip></el-table-column>
      <!--<el-table-column label="Link" prop="link" show-overflow-tooltip></el-table-column>-->
      <!--<el-table-column label="IP" prop="ip" width="130"></el-table-column>-->
      <el-table-column label="ReplyWho" prop="replyWho" width="100" show-overflow-tooltip></el-table-column>
      <el-table-column label="Content" prop="content" show-overflow-tooltip width="400"></el-table-column>
      <el-table-column label="ParentId" prop="parentId" width="130" show-overflow-tooltip>
        <!--跳转到评论所在页面-->
        <!--<template v-slot="scope">-->
        <!--  <el-link type="success" :href="`/blog/${scope.row.targetId}`" target="_blank">{{scope.row.targetId}}</el-link>-->
        <!--</template>-->
      </el-table-column>

      <el-table-column label="Time" width="115" show-overflow-tooltip>
        <template v-slot="scope">{{ scope.row.updateTime }}</template>
      </el-table-column>
      <el-table-column label="IsReleased" width="100">
        <template v-slot="scope">
          <el-switch v-model="scope.row.released" @change="commentPublishedChanged(scope.row)"></el-switch>
        </template>
      </el-table-column>

      <el-table-column label="Operate" width="200">
        <template v-slot="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">Edit</el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteCommentById(scope.row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
                   :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>

    <!--编辑评论对话框-->
    <el-dialog title="Edit Comment" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
      <!--内容主体-->
      <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="80px">
        <el-form-item label="Name" prop="nickname">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="Email" prop="email">
          <el-input v-model="editForm.email"></el-input>
        </el-form-item>
        <el-form-item label="Link" prop="website">
          <el-input v-model="editForm.link"></el-input>
        </el-form-item>
        <el-form-item label="IP" prop="ip">
          <el-input v-model="editForm.ip"></el-input>
        </el-form-item>
        <el-form-item label="Content" prop="content">
          <el-input v-model="editForm.content" type="textarea" maxlength="250" :rows="5" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="editDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="editComment">Confirm</el-button>
			</span>
    </el-dialog>
  </div>
</template>

<script>
import commentApi from '@/api/comment'
import {checkEmail} from "@/utils/regex";
import {checkIpv4} from "@/utils/regex";

export default {
  name: "CommentUnchecked",

  data() {
    return {
      pageId: null,
      queryInfo: { //查询搜索条件
        pageNum: 1,
        pageSize: 10,
        name: '',
        email: '',
        content: '',
        parentId: undefined,
      },
      total: 0,
      commentList: [],
      editDialogVisible: false,
      editForm: {
        id: '',
        name: '',
        email: '',
        link: '',
        ip: '',
        content: ''
      },
      formRules: {
        name: [{required: true, message: 'Please type name', trigger: 'blur'}],
        email: [
          {required: true, message: 'Please type email', trigger: 'blur'},
          {validator: checkEmail, trigger: 'blur'}
        ],
        ip: [
          {required: true, message: 'Please type IP', trigger: 'blur'},
           {validator: checkIpv4, trigger: 'blur'}
        ],
        content: [
          {required: true, message: 'Please type content', trigger: 'blur'},
          {max: 255, message: 'Content max length is 255', trigger: 'blur'}
        ],
      }
    }
  },
  created() {
    this.getCommentList()
  },
  methods: {
    getCommentList() {
      let param = {
        "data": {
          "currentPage": this.queryInfo.pageNum,
          "pageSize": this.queryInfo.pageSize,
          "condition": {
            "released": false, //未审核过了的、未发布的
            "deleted": false, //未删除的
          }
        }
      }
      commentApi.fetchList(param).then(res => {
        this.commentList = res.data.rows
        this.total = res.data.total
      })
    },

    commentSearch() {
      let param = {
        "data": {
          "currentPage": this.queryInfo.pageNum,
          "pageSize": this.queryInfo.pageSize,
          "condition": {
            "name": this.queryInfo.name,
            "email": this.queryInfo.email,
            "content": this.queryInfo.content,
            "parentId": this.queryInfo.parentId,
            "released": false, //未审核过了的、未发布的
            "deleted": false, //未删除的
          }
        }
      }
      commentApi.fetchList(param).then(res => {
        this.commentList = res.data.rows
        this.total = res.data.total
      })
    },

    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getCommentList()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getCommentList()
    },
    //切换评论公开状态（如果切换成隐藏，则该评论的所有子评论都修改为同样的隐藏状态）
    commentPublishedChanged(row) {
      let param = {
        "data": {
          "id": row.id,
          "released": row.released,
        }
      }
      commentApi.update(param).then(res => {
        this.$message.success(res.message)
        this.getCommentList() //成功后再获取新的数据
      })
    },

    deleteCommentById(id) {
      this.$confirm('It will delete <strong style="color: red">this Comment</strong>, Continue?', 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(() => {
        commentApi.del(id).then(res => {
          this.$message.success(res.message)
          this.getCommentList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Canceled'
        });
      });
    },
    showEditDialog(row) {
      this.editForm = {...row}
      this.editDialogVisible = true
    },
    editDialogClosed() {
      this.editForm = {}
      this.$refs.editFormRef.resetFields()
    },
    editComment() {
      this.$refs.editFormRef.validate(valid => {
        if (valid) {
          const param = {
            "data": {
              id: this.editForm.id,
              name: this.editForm.name,
              email: this.editForm.email,
              link: this.editForm.link,
              ip: this.editForm.ip,
              content: this.editForm.content,
            }
          }
          commentApi.update(param).then(res => {
            this.$message.success(res.msg)
            this.editDialogVisible = false
            this.getCommentList()
          })
        }
      })
    }
  }
}
</script>

<style scoped>

</style>