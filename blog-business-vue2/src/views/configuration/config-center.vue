<template>
  <div>
    <!--添加按钮-->
    <div style="margin: 5px 5px">
      <el-row :gutter="10">
        <el-col :span="6">
          <el-button type="primary" size="small" icon="el-icon-plus" @click="addDialogVisible=true">Add New Config</el-button>
        </el-col>
      </el-row>
    </div>

    <!--搜索-->
    <div style="margin-top: 10px">
      <el-form :inline="true" :model="queryInfo" class="demo-form-inline">
        <el-form-item label="Group">
          <el-input v-model="queryInfo.groupName" placeholder="Type Config Name"></el-input>
        </el-form-item>
        <el-form-item label="Key">
          <el-input v-model="queryInfo.configKey" placeholder="Type Config Key"></el-input>
        </el-form-item>
        <el-form-item label="Deleted">
          <el-radio v-model="queryInfo.deleted" :label="true">YES</el-radio>
          <el-radio v-model="queryInfo.deleted" :label="false">No</el-radio>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="search">Query</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!--列表展示-->
    <el-table :data="configList" row-key="id">
      <!--<el-table-column label="ID" prop="id" width="100" show-overflow-tooltip></el-table-column>-->
      <el-table-column label="No" type="index" width="50"></el-table-column>
      <el-table-column label="Group" prop="groupName" width="110" show-overflow-tooltip></el-table-column>
      <el-table-column label="GroupDesc" prop="groupDescription" width="110" show-overflow-tooltip></el-table-column>
      <el-table-column label="Key" prop="configKey" width="120" show-overflow-tooltip></el-table-column>
      <el-table-column label="Value" prop="configValue" width="180" show-overflow-tooltip></el-table-column>
      <el-table-column label="Description" prop="configDescription" width="180" show-overflow-tooltip></el-table-column>

      <el-table-column class-name="status-col" label="Deleted" width="90">
        <template v-slot="scope">
          <el-switch v-model="scope.row.deleted" @change="delConfig(scope.row)"></el-switch>
        </template>
      </el-table-column>

      <el-table-column label="Time" width="115" show-overflow-tooltip>
        <template v-slot="scope">{{ scope.row.updateTime }}</template>
      </el-table-column>

      <el-table-column label="Operate" width="200">
        <template v-slot="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">Edit</el-button>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="delConfigReal(scope.row)">Delete</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
                   :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>

    <!--添加标签对话框-->
    <el-dialog title="Add New Config" width="50%" :visible.sync="addDialogVisible" :close-on-click-modal="false" @close="addDialogClosed">
      <!--内容主体-->
      <el-form :model="addForm" :rules="formRules" ref="addFormRef" label-width="100px">
        <el-form-item label="Group" prop="groupName">
          <el-input v-model="addForm.groupName"></el-input>
        </el-form-item>
        <el-form-item label="GroupDesc" prop="groupDescription">
          <el-input v-model="addForm.groupDescription"></el-input>
        </el-form-item>
        <el-form-item label="Key" prop="configKey">
          <el-input v-model="addForm.configKey"></el-input>
        </el-form-item>
        <el-form-item label="Value" prop="configValue">
          <el-input v-model="addForm.configValue" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="Extend" prop="configExtend">
          <el-input v-model="addForm.configExtend" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="configDescription">
          <el-input v-model="addForm.configDescription" type="textarea"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer">
				<el-button @click="addDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="addNewConfig">Confirm</el-button>
			</span>
    </el-dialog>

    <!--编辑对话框-->
    <el-dialog title="Edit Link" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
      <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="100px" @close="editDialogClosed">
        <el-form-item label="Group" prop="groupName">
          <el-input v-model="editForm.groupName"></el-input>
        </el-form-item>
        <el-form-item label="GroupDesc" prop="groupDescription">
          <el-input v-model="editForm.groupDescription"></el-input>
        </el-form-item>
        <el-form-item label="Key" prop="configKey">
          <el-input v-model="editForm.configKey"></el-input>
        </el-form-item>
        <el-form-item label="Value" prop="configValue">
          <el-input v-model="editForm.configValue" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="Extend" prop="configExtend">
          <el-input v-model="editForm.configExtend" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="configDescription">
          <el-input v-model="editForm.configDescription" type="textarea"></el-input>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="editDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="editConfirm">Confirm</el-button>
			</span>
    </el-dialog>
  </div>
</template>

<script>
import configCenterApi from '@/api/configuration/config-center'

export default {
  name: "ConfigCenter",
  data() {
    return {
      pageId: null,
      queryInfo: { //查询搜索条件
        groupName: '',
        configKey: '',
        deleted: false,
        pageNum: 1,
        pageSize: 10,
      },
      total: 0,
      configList: [],
      addDialogVisible: false,
      editDialogVisible: false,
      showDialogVisible: false,
      editForm: {},
      addForm: {
        groupName: '',
        groupDescription: '',
        configKey: '',
        configValue: '',
        configExtend: '',
        configDescription: '',
      },
      formRules: {
        groupName: [{required: true, message: 'Please Input Config Group', trigger: 'blur'}],
        configKey: [{required: true, message: 'Please Input Config Key', trigger: 'blur'}],
        configValue: [{required: true, message: 'Please Input Config Value', trigger: 'blur'}],
      },
    }
  },
  created() {
    this.getConfigList()
  },
  methods: {
    getConfigList() {
      let param = {
        "data": {
          "currentPage": this.queryInfo.pageNum,
          "pageSize": this.queryInfo.pageSize,
          "condition": {
            "groupName": "",
            "configKey": "",
            "deleted": false,
          }
        }
      }
      configCenterApi.fetchList(param).then(res => {
        this.configList = res.data.rows
        this.total = res.data.total
      })
    },

    search() {
      let param = {
        "data": {
          "currentPage": 1,  //搜索一定是从第一页开始
          "pageSize": this.queryInfo.pageSize,
          "condition": {
            "groupName": this.queryInfo.groupName,
            "configKey": this.queryInfo.configKey,
            "deleted": this.queryInfo.deleted,
          }
        }
      }
      configCenterApi.fetchList(param).then(res => {
        this.configList = res.data.rows
        this.total = res.data.total
      })
    },

    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getConfigList()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getConfigList()
    },
    addDialogClosed() {
      //重置表单到初始值，不是清空表单
      //如果resetFields()没有生效，是因为没有设置有效验证规则。解决：设置验证规则 prop
      //也就是说：el-form-item的prop值要与el-input v-model的值一致
      this.$refs.addFormRef.resetFields()
    },
    addNewConfig() {
      this.$refs.addFormRef.validate(valid => {
        if (valid) {
          let param = {
            "data": {
              "groupName": this.addForm.groupName,
              "groupDescription": this.addForm.groupDescription,
              "configKey": this.addForm.configKey,
              "configValue": this.addForm.configValue,
              "configExtend": this.addForm.configExtend,
              "configDescription": this.addForm.configDescription,
            }
          }
          configCenterApi.add(param).then(res => {
            this.$message.success(res.message)
            this.addDialogVisible = false
            this.getConfigList()
          })
        }
      })
    },
    delConfigReal(row) {
      this.$confirm('It will delete <strong style="color: red">this config</strong>, Continue?', 'Notice', {
        confirmButtonText: 'Confirm',
        cancelButtonText: 'Cancel',
        type: 'warning',
        dangerouslyUseHTMLString: true
      }).then(() => {
        configCenterApi.del(row.id).then(res => {
          this.$message.success(res.message)
          this.getConfigList()
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: 'Canceled'
        });
      });
    },
    delConfig(row) {
      let param = {
        "data": {
          "id": row.id,
          "deleted":row.deleted
        }
      }
      configCenterApi.update(param).then(res => {
        this.$message(res.message);
        this.getConfigList()
      })
    },
    showEditDialog(row) {
      this.editForm = {...row}
      this.editDialogVisible = true
    },
    editDialogClosed() {
      this.editForm = {}
      this.$refs.editFormRef.resetFields()
    },
    editConfirm() {
      this.$refs.editFormRef.validate(valid => {
        if (valid) {
          const param = {
            "data": {
              "id": this.editForm.id,
              "groupName": this.editForm.groupName,
              "groupDescription": this.editForm.groupDescription,
              "configKey": this.editForm.configKey,
              "configValue": this.editForm.configValue,
              "configExtend": this.editForm.configExtend,
              "configDescription": this.editForm.configDescription,
            }
          }
          configCenterApi.update(param).then(res => {
            this.$message.success(res.message)
            this.editDialogVisible = false
            this.getConfigList()
          })
        }
      })
    },
  }
}
</script>

<style>

</style>
