<template>
  <div>
    <!--添加-->
    <el-row :gutter="10">
      <el-col :span="6">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="addDialogVisible=true">Add Tag</el-button>
      </el-col>
    </el-row>

    <el-table :data="tagList">
      <el-table-column label="No" type="index" width="50"></el-table-column>
      <el-table-column label="Name" prop="name" width="100"></el-table-column>
      <el-table-column label="code" prop="code" width="80"></el-table-column>
      <el-table-column label="Description" prop="description"></el-table-column>
      <el-table-column label="Color" width="100">
        <template v-slot="scope">
          <span style="float:left;width: 100px;">{{ scope.row.color }}</span>
          <span style="float:left;width: 100px; height: 23px" :class="`me-${scope.row.color}`"></span>
        </template>
      </el-table-column>

      <el-table-column label="UpdateTime" prop="updateTime"></el-table-column>

      <el-table-column label="Operate">
        <template v-slot="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="showEditDialog(scope.row)">Edit</el-button>
          <el-popconfirm title="Are you sure to delete?" icon="el-icon-delete" iconColor="red" @confirm="deleteTagById(scope.row.id)">
            <el-button size="mini" type="danger" icon="el-icon-delete" slot="reference">Delete</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.pageNum"
                   :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>

    <!--添加标签对话框-->
    <el-dialog title="Add Tag" width="50%" :visible.sync="addDialogVisible" :close-on-click-modal="false" @close="addDialogClosed">
      <!--内容主体-->
      <el-form :model="addForm" :rules="formRules" ref="addFormRef" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="addForm.name"></el-input>
        </el-form-item>
        <el-form-item label="Code" prop="code">
          <el-input v-model="addForm.code"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="addForm.description"></el-input>
        </el-form-item>
        <el-form-item label="Color">
          <el-select v-model="addForm.color" placeholder="Choose Color" :clearable="true" style="width: 100%">
            <el-option v-for="item in colors" :key="item.value" :label="item.label" :value="item.value">
              <span style="float: left; width: 100px;">{{ item.label }}</span>
              <span style="float: left; width: 100px; height: inherit" :class="`me-${item.value}`"></span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.value }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="addDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="addTag">Confirm</el-button>
			</span>
    </el-dialog>

    <!--编辑标签对话框-->
    <el-dialog title="Edit Tag" width="50%" :visible.sync="editDialogVisible" :close-on-click-modal="false" @close="editDialogClosed">
      <!--内容主体-->
      <el-form :model="editForm" :rules="formRules" ref="editFormRef" label-width="100px">
        <el-form-item label="Name" prop="name">
          <el-input v-model="editForm.name"></el-input>
        </el-form-item>
        <el-form-item label="Code" prop="code">
          <el-input v-model="editForm.code"></el-input>
        </el-form-item>
        <el-form-item label="Description" prop="description">
          <el-input v-model="editForm.description"></el-input>
        </el-form-item>
        <el-form-item label="Color" prop="color">
          <el-select v-model="editForm.color" placeholder="Choose Color" :clearable="true" style="width: 100%">
            <el-option v-for="item in colors" :key="item.value" :label="item.label" :value="item.value">
              <span style="float: left; width: 100px;">{{ item.label }}</span>
              <span style="float: left; width: 100px; height: inherit" :class="`me-${item.value}`"></span>
              <span style="float: right; color: #8492a6; font-size: 13px">{{ item.value }}</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <!--底部-->
      <span slot="footer">
				<el-button @click="editDialogVisible=false">Cancel</el-button>
				<el-button type="primary" @click="editTag">Confirm</el-button>
			</span>
    </el-dialog>
  </div>
</template>

<script>
import tagApi from '@/api/article/tag'

export default {
  name: "TagList",
  data() {
    return {
      queryInfo: {
        pageNum: 1,
        pageSize: 10
      },
      tagList: [],
      total: 0,
      addDialogVisible: false,
      editDialogVisible: false,
      addForm: {
        name: '',
        code: '',
        description: '',
        color: '',
      },
      editForm: {},
      formRules: {
        name: [{required: true, message: 'Please Input Tag Name', trigger: 'blur'}],
        code: [{required: true, message: 'Please Input Tag Code', trigger: 'blur'}],
      },
      colors: [
        {label: '红色', value: 'red'},
        {label: '橘黄', value: 'orange'},
        {label: '黄色', value: 'yellow'},
        {label: '橄榄绿', value: 'olive'},
        {label: '纯绿', value: 'green'},
        {label: '水鸭蓝', value: 'teal'},
        {label: '纯蓝', value: 'blue'},
        {label: '紫罗兰', value: 'violet'},
        {label: '紫色', value: 'purple'},
        {label: '粉红', value: 'pink'},
        {label: '棕色', value: 'brown'},
        {label: '灰色', value: 'grey'},
        {label: '黑色', value: 'black'},
      ],
    }
  },
  created() {
    this.getData()
  },
  methods: {
    getData() {
      let param = {
        "data": {
          "currentPage": this.queryInfo.pageNum,
          "pageSize": this.queryInfo.pageSize
        }
      }
      tagApi.fetchList(param).then(res => {
        this.tagList = res.data.rows
        this.total = res.data.total
      })
    },
    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.getData()
    },
    //监听页码改变事件
    handleCurrentChange(newPage) {
      this.queryInfo.pageNum = newPage
      this.getData()
    },
    addDialogClosed() {
      this.addForm.color = ''
      //重置表单到初始值，不是清空表单
      //如果resetFields()没有生效，是因为没有设置有效验证规则。解决：设置验证规则 prop
      //也就是说：el-form-item的prop值要与el-input v-model的值一致
      this.$refs.addFormRef.resetFields()
    },
    editDialogClosed() {
      this.editForm = {}
      this.$refs.editFormRef.resetFields()
    },
    addTag() {
      this.$refs.addFormRef.validate(valid => {
        if (valid) {
          let param = {
            "data": {
              "name": this.addForm.name,
              "code": this.addForm.code,
              "description": this.addForm.description,
              "color": this.addForm.color,
            }
          }
          tagApi.add(param).then(res => {
            this.$message.success(res.message)
            this.addDialogVisible = false
            this.getData()
          })

        }
      })
    },
    editTag() {
      this.$refs.editFormRef.validate(valid => {
        if (valid) {
          let param = {
            "data": {
              "id": this.editForm.id,
              "name": this.editForm.name,
              "code": this.editForm.code,
              "description": this.editForm.description,
              "color": this.editForm.color
            }
          }
          tagApi.update(param).then(res => {
            this.$message.success(res.message)
            this.editDialogVisible = false
            this.getData()
          })
        }
      })
    },
    showEditDialog(row) {
      this.editForm = {...row}
      this.editDialogVisible = true
    },
    deleteTagById(id) {
      tagApi.del(id).then(res => {
        this.$message.success(res.message)
        this.getData()
      })
    }
  }
}
</script>

<style scoped>
.el-button + span {
  margin-left: 10px;
}
</style>