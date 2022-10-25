<!--
  已发布文章列表
-->
<template>
  <div>
    <!--搜索-->
    <el-row>
      <el-col :span="8">
        <el-input placeholder="Type Title" v-model="queryInfo.title" :clearable="true" @clear="search" @keyup.native.enter="search" size="small" style="min-width: 500px">
          <el-select v-model="queryInfo.categoryId" slot="prepend" placeholder="Choose Category"
                     :clearable="true" :filterable="true" @change="search" style="width: 160px">
            <el-option :label="item.name" :value="item.id" v-for="item in categoryList" :key="item.id"></el-option>
          </el-select>
          <el-button slot="append" icon="el-icon-search" @click="search"></el-button>
        </el-input>
      </el-col>
    </el-row>

    <!--表格显示-->
    <el-table :data="articleList">
      <el-table-column label="No" type="index" width="50"></el-table-column>
      <el-table-column label="Title" prop="title" show-overflow-tooltip></el-table-column>
      <el-table-column label="Category" prop="categories" width="120"></el-table-column>
      <el-table-column label="Author" prop="authors" width="100"></el-table-column>
      <el-table-column label="Version" prop="version" width="80"></el-table-column>

      <el-table-column class-name="status-col" label="Published" width="90">
        <template v-slot="scope">
          <el-switch v-model="scope.row.released" @change="changePublishedStatus(scope.row)"></el-switch>
        </template>
      </el-table-column>

      <el-table-column label="Update Time" width="150">
        <template v-slot="scope">{{ scope.row.updateTime }}</template>
      </el-table-column>
      <el-table-column label="Operate" width="200">
        <template v-slot="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="goBlogEditPage(scope.row.id)">Edit</el-button>
          <el-popconfirm title="Are you sure to delete?" icon="el-icon-delete" iconColor="red" @confirm="deleteBlogById(scope.row.id)">
            <el-button size="mini" type="danger" icon="el-icon-delete" slot="reference">Delete</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!--分页-->
    <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="queryInfo.currentPage"
                   :page-sizes="[10, 20, 30, 50]" :page-size="queryInfo.pageSize" :total="total"
                   layout="total, sizes, prev, pager, next, jumper" background>
    </el-pagination>
  </div>
</template>


<script>
import articleApi from '@/api/article/article'
import categoryApi from '@/api/article/category'

export default {
  name: "ArticleReleased",
  data() {
    return {
      queryInfo: {
        title: '',
        categoryId: null,
        currentPage: 1,
        pageSize: 10
      },
      //文章列表
      articleList: [],
      //分类列表
      categoryList: [],
      total: 0,
      articleId: 0,
      visForm: {
        appreciation: false,
        recommend: false,
        commentEnabled: false,
        top: false,
        published: false,
        password: '',
      }
    }
  },
  created() {
    this.fetchArticleData()
    //console.log("this.articleList", this.articleList)
  },
  methods: {
    fetchArticleData() {
      let param = {
        "data": {
          "currentPage": this.queryInfo.currentPage,
          "pageSize": this.queryInfo.pageSize,
          //查询条件、过滤条件
          "condition": {
            "released": true, //已发布
            "deleted": false, //未删除
            "categoryId": this.queryInfo.categoryId,
            "title": this.queryInfo.title,
          }
        }
      }

      //分页获取文章列表
      articleApi.fetchList(param).then(res => {
        let tmpArticleList = res.data.rows

        tmpArticleList.forEach(article => {
          let tmpAuthors = ''
          article.authors.forEach(ele => {
            tmpAuthors += ele.nickName + " "
          })
          article.authors = tmpAuthors

          let tmpCategories = ''
          article.categories.forEach(ele => {
            tmpCategories += ele.name + " "
          })
          article.categories = tmpCategories

          let tmpTags = ''
          article.tags.forEach(ele => {
            tmpTags += ele.name + " "
          })
          article.tags = tmpTags
        })

        this.articleList = tmpArticleList
        this.total = res.data.total
      })

      //获取所有分类
      categoryApi.fetchAll().then(resp => {
        this.categoryList = resp.data
      })
    },

    search() {
      this.queryInfo.currentPage = 1
      this.queryInfo.pageSize = 10
      this.fetchArticleData()
    },
    //切换文章发布状态
    changePublishedStatus(row) {
      let param = {
        "data": {
          "id": row.id,
          "released":row.released
        }
      }
      articleApi.update(param).then(res => {
        this.$message.success(res.message);
        this.fetchArticleData();
      })
    },
    //监听 pageSize 改变事件
    handleSizeChange(newSize) {
      this.queryInfo.pageSize = newSize
      this.fetchArticleData()
    },
    //监听页码改变的事件
    handleCurrentChange(newPage) {
      this.queryInfo.currentPage = newPage
      this.fetchArticleData()
    },
    //路由到编辑页
    goBlogEditPage(id) {
      this.$router.push('/article/edit/'+id)
    },
    deleteBlogById(id) {
      //逻辑删除
      articleApi.del(id).then(res => {
        this.$message.success(res.message)
        this.fetchArticleData()
      })
      //this.$confirm('It will delete this Article<strong style="color: red">and all child</strong>, Continue?', 'Notice', {
      //  confirmButtonText: 'Confirm',
      //  cancelButtonText: 'Cancel',
      //  type: 'warning',
      //  dangerouslyUseHTMLString: true
      //}).then(() => {
      //  console.log("del")
      //  articleApi.del(id).then(res => {
      //    this.$message.success(res.message)
      //    this.fetchArticleData()
      //  })
      //}).catch(() => {
      //  this.$message({
      //    type: 'info',
      //    message: 'Canceled'
      //  })
      //})
    }
  }
}
</script>

<style scoped>
.el-button + span {
  margin-left: 10px;
}
</style>