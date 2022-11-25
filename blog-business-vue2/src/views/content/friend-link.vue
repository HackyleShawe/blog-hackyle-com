<!--
  友链管理
-->
<template>
  <div>
    <!--添加按钮-->
    <el-row :gutter="10">
      <el-col :span="6">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="addDialogVisible=true">Add New Friend Link</el-button>
      </el-col>
    </el-row>

    <!--搜索-->
    <div style="margin-top: 10px">
      <el-form :inline="true" :model="queryInfo" class="demo-form-inline">
        <el-form-item label="Name">
          <el-input v-model="queryInfo.name" placeholder="Type Name"></el-input>
        </el-form-item>
        <el-form-item label="LinkURL">
          <el-input v-model="queryInfo.linkUrl" placeholder="Type Link URL"></el-input>
        </el-form-item>
        <el-form-item label="Description">
          <el-input v-model="queryInfo.description" placeholder="Type Description"></el-input>
        </el-form-item>
        <el-form-item label="RankWeight">
          <el-input v-model="queryInfo.rankWeight" placeholder="Type Rank Weight"
                    type="number" value="0" step="5" min="-999" max="999"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="search">Query</el-button>
        </el-form-item>

        <el-form-item label="Deleted">
          <el-radio v-model="queryInfo.deleted" :label="true">YES</el-radio>
          <el-radio v-model="queryInfo.deleted" :label="false">NO</el-radio>
          <el-radio v-model="queryInfo.deleted" :label="undefined">UnSelected</el-radio>
        </el-form-item>
        <el-form-item label="Released">
          <el-radio v-model="queryInfo.released" :label="true">YES</el-radio>
          <el-radio v-model="queryInfo.released" :label="false">NO</el-radio>
          <el-radio v-model="queryInfo.released" :label="undefined">UnSelected</el-radio>
        </el-form-item>
      </el-form>
    </div>

    <!--友链展示列表-->
    <el-table :data="friendLinkList" row-key="id" :tree-props="{children: 'replyComments'}">
      <el-table-column label="ID" prop="id" width="50" show-overflow-tooltip></el-table-column>
      <el-table-column label="Name" prop="name" width="80" show-overflow-tooltip></el-table-column>
      <el-table-column label="LinkURL" prop="linkUrl" width="150" show-overflow-tooltip></el-table-column>
      <el-table-column label="Description" prop="description" width="180" show-overflow-tooltip></el-table-column>
      <el-table-column label="Avatar" prop="linkAvatarUrl" width="150" show-overflow-tooltip></el-table-column>
      <el-table-column label="RankWeight" prop="rankWeight" width="120" show-overflow-tooltip></el-table-column>

      <el-table-column label="Time" width="115">
        <template v-slot="scope">{{ scope.row.updateTime }}</template>
      </el-table-column>
      <el-table-column label="IsDeleted" width="85">
        <template v-slot="scope">
          <el-switch v-model="scope.row.deleted" @change="friendLinkDeletedChanged(scope.row)"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="IsReleased" width="100">
        <template v-slot="scope">
          <el-switch v-model="scope.row.released" @change="linkPublishedChanged(scope.row)"></el-switch>
        </template>
      </el-table-column>

      <el-table-column label="Operate" width="200">
        <template v-slot="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">Edit</el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteLinkById(scope.row.id)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
                   :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>

    <!--添加对话框-->
    <el-dialog title="Add New Author" width="50%" :visible.sync="addDialogVisible" :close-on-click-modal="false" @close="addDialogClosed">
      <!--内容主体-->
      <el-form :model="addForm" :rules="formRules" ref="addFormRef" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="addForm.name"></el-input>
        </el-form-item>
        <el-form-item label="LinkURL" prop="linkUrl">
          <el-input v-model="addForm.linkUrl"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="addForm.description"></el-input>
        </el-form-item>
        <el-form-item label="Avatar URL" prop="linkAvatarUrl">
          <el-input v-model="addForm.linkAvatarUrl"></el-input>
        </el-form-item>
        <el-form-item label="RankWeight" prop="rankWeight">
          <el-input v-model="addForm.rankWeight" placeholder="Type Rank Weight"
                    type="number" value="0" step="5" min="-999" max="999"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer">
				<el-button @click="addDialogVisible=false">取 消</el-button>
				<el-button type="primary" @click="addLink">确 定</el-button>
			</span>
    </el-dialog>

    <!--编辑友链对话框-->
    <el-dialog title="Edit Link" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
      <!--内容主体-->
      <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="100px">
        <el-form-item label="Name" prop="nickname">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="LinkURL" prop="linkUrl">
          <el-input v-model="editForm.linkUrl"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="editForm.description"></el-input>
        </el-form-item>
        <el-form-item label="Avatar URL" prop="linkAvatarUrl">
          <el-input v-model="editForm.linkAvatarUrl"></el-input>
        </el-form-item>
        <el-form-item label="RankWeight" prop="rankWeight">
          <el-input v-model="addForm.rankWeight" placeholder="Type Rank Weight"
                    type="number" value="0" step="5" min="-999" max="999"></el-input>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="editDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="editLink">Confirm</el-button>
			</span>
    </el-dialog>

  </div>
</template>

<script>
import friendLinkApi from '@/api/content/friend-link'

export default {
  name: "FriendLink",

  data() {
    return {
      pageId: null,
      queryInfo: { //查询搜索条件
        pageNum: 1,
        pageSize: 10,
        name: '',
        linkUrl: '',
        description: '',
        rankWeight: 0,
        deleted: undefined, //是否删除
        released: undefined, //是否已经审查发布
      },
      total: 0,
      friendLinkList: [],
      editDialogVisible: false,
      addDialogVisible: false,
      addForm: {
        name: '',
        linkUrl: '',
        rankWeight: 0,
        description: '',
        linkAvatarUrl: '',
      },
      editForm: {
        id: 0,
        name: '',
        linkUrl: '',
        rankWeight: 0,
        description: '',
        linkAvatarUrl: '',
      },
      formRules: {
        name: [{required: true, message: 'Please type name', trigger: 'blur'}],
        linkUrl: [
          {required: true, message: 'Please type link URL', trigger: 'blur'},
          //{validator: checkEmail, trigger: 'blur'}
        ],
        //linkAvatarUrl: [
        //  {required: true, message: 'Please type avatar URL', trigger: 'blur'},
        //  {validator: checkEmail, trigger: 'blur'}
        //],
        //rankWeight: [
          //{required: true, message: 'Please type avatar URL', trigger: 'blur'},
          //{min: 0, message: 'The rank weight can\'t be less than 0', trigger: 'blur'}
        //],
        description: [
          {required: true, message: 'Please type description', trigger: 'blur'},
          //{max: 255, message: 'Content max length is 255', trigger: 'blur'}
        ],
      }
    }
  },
  created() {
    this.getFriendLinkList()
  },
  methods: {
    getFriendLinkList() {
      let param = {
        "data": {
          "currentPage": this.queryInfo.pageNum,
          "pageSize": this.queryInfo.pageSize,
          "condition": {
            "name": this.queryInfo.name,
            "linkUrl": this.queryInfo.linkUrl,
            "description": this.queryInfo.description,
            "rankWeight": this.queryInfo.rankWeight,
            "deleted": this.queryInfo.deleted, //是否删除
            "released": this.queryInfo.released, //是否已经审查发布
          }
        }
      }
      friendLinkApi.fetchList(param).then(res => {
        this.friendLinkList = res.data.rows
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
            "linkUrl": this.queryInfo.linkUrl,
            "description": this.queryInfo.description,
            "rankWeight": this.queryInfo.rankWeight,
            "deleted": this.queryInfo.deleted, //是否删除
            "released": this.queryInfo.released, //是否已经审查发布
          }
        }
      }
      friendLinkApi.fetchList(param).then(res => {
        this.friendLinkList = res.data.rows
        this.total = res.data.total
      })
    },

    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getFriendLinkList()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getFriendLinkList()
    },
    addDialogClosed() {
      //重置表单到初始值，不是清空表单
      //如果resetFields()没有生效，是因为没有设置有效验证规则。解决：设置验证规则 prop
      //也就是说：el-form-item的prop值要与el-input v-model的值一致
      this.$refs.addFormRef.resetFields()
    },

    //切换友链公开状态（如果切换成隐藏，则该友链的所有子友链都修改为同样的隐藏状态）
    friendLinkDeletedChanged(row) {
      let param = {
        "data": {
          "id": row.id,
          "deleted": row.deleted,
        }
      }
      friendLinkApi.update(param).then(res => {
        this.$message.success(res.message)
        this.getFriendLinkList() //成功后再获取新的数据
      })
    },
    //切换友链公开状态（如果切换成隐藏，则该友链的所有子友链都修改为同样的隐藏状态）
    linkPublishedChanged(row) {
      let param = {
        "data": {
          "id": row.id,
          "released": row.released,
        }
      }
      friendLinkApi.update(param).then(res => {
        this.$message.success(res.message)
        this.getCommentList() //成功后再获取新的数据
      })
    },
    deleteLinkById(id) {
      this.$confirm('It will delete <strong style="color: red">this friend link</strong>, Continue?', 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(() => {
        friendLinkApi.del(id).then(res => {
          this.$message.success(res.message)
          this.getFriendLinkList()
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
    addLink() {
      this.$refs.addFormRef.validate(valid => {
        if (valid) {
          let param = {
            "data": {
              "name": this.addForm.name,
              "linkUrl": this.addForm.linkUrl,
              "description": this.addForm.description,
              "rankWeight": this.addForm.rankWeight,
              "linkAvatarUrl": this.addForm.linkAvatarUrl,
            }
          }
          friendLinkApi.add(param).then(res => {
            this.$message.success(res.message)
            this.addDialogVisible = false
            this.getFriendLinkList()
          })

        }
      })
    },
    editLink() {
      this.$refs.editFormRef.validate(valid => {
        if (valid) {
          const param = {
            "data": {
              id: this.editForm.id,
              name: this.editForm.name,
              linkUrl: this.editForm.linkUrl,
              description: this.editForm.description,
              linkAvatarUrl: this.editForm.linkAvatarUrl,
              rankWeight: this.editForm.rankWeight,
            }
          }
          friendLinkApi.update(param).then(res => {
            this.$message.success(res.message)
            this.editDialogVisible = false
            this.getFriendLinkList()
          })
        }
      })
    },
  }
}
</script>

<style scoped>

</style>
