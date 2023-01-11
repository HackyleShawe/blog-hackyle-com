<!--
  文章编辑器：用于创建和修改文件内容
-->
<template>
  <div class="createPost-container">
    <el-form ref="articleDataForm" :model="articleDataForm" :rules="rules" class="form-container">
      <!--顶部固定栏-->
      <sticky :z-index="10" :class-name="'sub-navbar '+articleDataForm.status">
        <is-comment v-model="articleDataForm.commented" style="margin-left: 50px;" />
        <is-release v-model="articleDataForm.released" style="margin-left: 10px;" />
        <!--<is-new-version v-model="articleDataForm.newVersion" style="margin-left: 10px;" />-->

        <!--保存文章-->
        <el-button v-loading="loading" style="margin-left: 10px;" type="success" @click="submitForm">
          SAVE
        </el-button>
      </sticky>

      <div class="createPost-main-container">
        <el-form-item style="margin-bottom: 2px;" label-width="40px" label="Title:">
          <el-input v-model="articleDataForm.title" :rows="1" type="textarea"
                    class="article-textarea" autosize placeholder="Please enter the article title" />
          <span v-show="titleContentLength" class="word-counter">{{ titleContentLength }}words</span>
        </el-form-item>

        <el-form-item style="margin-bottom: 2px;" label-width="70px" label="Summary:">
          <el-input v-model="articleDataForm.summary" :rows="1" type="textarea"
                    class="article-textarea" autosize placeholder="Please enter the article summery" />
          <span v-show="summaryContentLength" class="word-counter">{{ summaryContentLength }}words</span>
        </el-form-item>

        <el-form-item style="margin-bottom: 2px;" label-width="70px" label="Keywords:">
          <el-input v-model="articleDataForm.keywords" :rows="1" type="textarea"
                    class="article-textarea" autosize placeholder="Please enter the article keywords as meta tag of keywords for SEO" />
          <span v-show="keywordsContentLength" class="word-counter">{{ keywordsContentLength }}words</span>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Authors" prop="authorList">
              <!--<select-authors :initAuthors="articleDataForm.authors" @chooseAuthor="chooseAuthorHandler"/>-->
              <el-select v-model="articleDataForm.authors" placeholder="Please select authors"
                         :filterable="true" :multiple="true"
                         style="width: 100%;" value-key="id">
                <el-option :label="author.nickName" :value="author" v-for="author in authorListOptions" :key="author.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="URI" prop="uri">
              <el-input v-model="articleDataForm.uri" :rows="1" type="input"
                        class="article-textarea" autosize placeholder="Please enter the article URI (https://domain.com/category-code/uri)" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Categories" prop="categoryList">
              <!--<select-categories :initCategories="articleDataForm.categories" @chooseCategory="chooseCategoryHandler" />-->
              <el-select v-model="articleDataForm.categories" placeholder="Please select categories"
                         :filterable="true" :multiple="true"
                         style="width: 100%;" value-key="id">
                <el-option :label="category.name" :value="category" v-for="category in categoryListOptions" :key="category.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Tags" prop="tagList">
              <!--<select-tags :initTags="articleDataForm.tags" @chooseTags="chooseTagHandler"/>-->
              <el-select v-model="articleDataForm.tags" placeholder="Please select tags"
                         :filterable="true" :multiple="true"
                         style="width: 100%;" value-key="id">
                <!--NOTICE：v-model绑定的是一个对象，option里面也要绑定一个对象--->
                <!--https://segmentfault.com/q/1010000019113355-->
                <el-option :label="tag.name" :value="tag" v-for="tag in tagListOptions" :key="tag.id"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <!--TinyMCE富文本编辑器-->
        <el-form-item prop="content" style="margin-bottom: 30px;">
          <Tinymce ref="editor" v-model="articleDataForm.content" :height="400" />
        </el-form-item>

        <!--底部的图片上传组件-->
        <p>上传封面图片</p>
        <el-form-item prop="image_uri" style="margin-bottom: 30px;">
          <Upload v-model="articleDataForm.faceImgLink" />
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script>

//导入公共组件
import Tinymce from '@/components/Tinymce' //富文本TinyMCE组件
import Upload from '@/components/UploadImage/SingleImage3' //图片上传组件
import Sticky from '@/components/Sticky' //Header黏贴组件：当页面滚动时，固定header不动

//导入子组件
import IsComment from './IsComment'
import IsRelease from './IsRelease'
//import IsNewVersion from './IsNewVersion'
//import SelectCategories from "./SelectCategories"
//import SelectTags from "./SelectTags"
//import SelectAuthors from './SelectAuthors'

//导入JS
import { isExternal } from '@/utils/validate'
import articleApi from '@/api/article/article'
import categoryApi from "@/api/article/category";
import tagApi from "@/api/article/tag";
import authorApi from "@/api/article/author";

export default {
  name: 'ArticleEditor',
  components: {Tinymce, Upload, Sticky, IsRelease, IsComment},
  props: {
    isEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    const validateRequire = (rule, value, callback) => {
      if (value === '') {
        this.$message({
          message: rule.field + '为必传项',
          type: 'error'
        })
        callback(new Error(rule.field + '为必传项'))
      } else {
        callback()
      }
    }
    const validateSourceUri = (rule, value, callback) => {
      if (value) {
        if (isExternal(value)) {
          callback()
        } else {
          this.$message({
            message: '外链url填写不正确',
            type: 'error'
          })
          callback(new Error('外链url填写不正确'))
        }
      } else {
        callback()
      }
    }
    return {
      articleDataForm: {
        id: "",
        uri: "", //文章外链
        authors: [], //作者
        categories: [], //文章分类
        tags: [], //文章标签
        title: '', //文章题目
        summary: '', //文章摘要
        keywords: '', //文章关键字，直接用于meta标签，SEO
        content: '', //文章内容
        faceImgLink: '', //文章图片
        released: false, //是否发布
        commented: true, //是否可以评论
        newVersion: false //是否保存为新版本
      },
      loading: false,
      //submitted: false, //是否已提交，用于防止多次发送新增文章请求。不设置这个限定条件，方便随时保存
      categoryListOptions: [], //所有的分类
      authorListOptions: [], //所有的作者
      tagListOptions: [], //所有的标签
      rules: {
        //image_uri: [{ validator: validateRequire }],
        title: [{ validator: validateRequire }],
        content: [{ validator: validateRequire }],
        linkUri: [{ validator: validateSourceUri, trigger: 'blur' }]
      },
      tempRoute: {}
    }
  },
  computed: {
    titleContentLength() {
      return this.articleDataForm.title.length
    },
    summaryContentLength() {
      return this.articleDataForm.summary.length
    },
    keywordsContentLength() {
      return this.articleDataForm.keywords.length
    },

    displayTime: {
      // set and get is useful when the data
      // returned by the back end api is different from the front end
      // back end return => "2013-06-25 06:59:25"
      // front end need timestamp => 1372114765000
      get() {
        return (+new Date(this.articleDataForm.display_time))
      },
      set(val) {
        this.articleDataForm.display_time = new Date(val)
      }
    }
  },
  created() {
    if (this.isEdit) {
      const id = this.$route.params && this.$route.params.id
      this.articleDataForm.id = this.$route.params.id
      this.fetchArticleData(id)
    }

    //获取选项栏的所有数据
    this.getAllCategory()
    this.getAllTag()
    this.getAllAuthor()

    // Why need to make a copy of this.$route here?
    // Because if you enter this page and quickly switch tag, may be in the execution of the setTagsViewTitle function, this.$route is no longer pointing to the current page
    // https://github.com/PanJiaChen/vue-element-admin/issues/1221
    this.tempRoute = Object.assign({}, this.$route)
  },
  methods: {
    fetchArticleData(id) {
      articleApi.fetch(id).then(response => {
        this.articleDataForm = response.data
        //console.log("this.articleDataForm:", this.articleDataForm)

        this.setTagsViewTitle() // set tagsview title
        this.setPageTitle() // set page title
      }).catch(err => {
        console.log(err)
      })
    },
    setTagsViewTitle() {
      const title = 'Edit'
      //提取ID的前10个字符，展示在路由页上
      const route = Object.assign({}, this.tempRoute, { title: `${title}-${this.articleDataForm.id.substr(0, 10)}` })
      this.$store.dispatch('tagsView/updateVisitedView', route)
    },
    setPageTitle() {
      const title = 'Edit Article'
      document.title = `${title} - ${this.articleDataForm.id}`
    },

    getAllCategory() {
      categoryApi.fetchAll().then(resp => {
        this.categoryListOptions = []
        this.categoryListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },
    getAllTag() {
      tagApi.fetchAll().then(resp => {
        this.tagListOptions = []
        this.tagListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },
    getAllAuthor() {
      authorApi.fetchAll().then(resp => {
        this.authorListOptions = []
        this.authorListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },

    //表单提交
    submitForm() {
      let authorIds = "";
      let categoryIds = "";
      let tagIds = "";

      //console.log("this.articleDataForm.categories: ", this.articleDataForm.categories)
      //console.log("this.articleDataForm.authors: ", this.articleDataForm.authors)
      //console.log("this.articleDataForm.tags: ", this.articleDataForm.tags)

      this.articleDataForm.categories.forEach(ele => {
        categoryIds += ele.id + ","
      })
      this.articleDataForm.authors.forEach(ele => {
        authorIds += ele.id + ","
      })
      this.articleDataForm.tags.forEach(ele => {
        tagIds += ele.id + ","
      })

      //准备请求参数
      let param = {
        "data": {
          "id": this.articleDataForm.id,
          "authorIds": authorIds,
          "categoryIds": categoryIds,
          "tagIds": tagIds,
          "title": this.articleDataForm.title,
          "uri": this.articleDataForm.uri,
          "summary": this.articleDataForm.summary,
          "keywords": this.articleDataForm.keywords,
          "content": this.articleDataForm.content,
          "linkUri": this.articleDataForm.linkUri,
          "faceImgLink": this.articleDataForm.faceImgLink,
          "released": this.articleDataForm.released,
          "commented": this.articleDataForm.commented,
          "newVersion": this.articleDataForm.newVersion,
        }
      }

      //编辑模式
      if (this.isEdit) {
        articleApi.update(param).then(resp => {
          console.log(resp)
          this.$notify({
            title: 'SUCCESS',
            message: '编辑文章保存成功',
            type: 'success',
            duration: 2000
          })
        }).catch(err => {
          console.log(err)
          this.$notify({
            title: '失败',
            message: '编辑文章保存失败',
            type: 'error',
            duration: 2000
          })
        });
      } else { //新增模式
        articleApi.add(param).then(resp => {
          console.log(resp)
          this.$notify({
            title: '成功',
            message: '新建文章保存成功',
            type: 'success',
            duration: 2000
          })
        }).catch(err => {
          console.log(err)
          this.$notify({
            title: '失败',
            message: '新建文章保存失败',
            type: 'error',
            duration: 2000
          })
        });
      }

    },
  }
}
</script>

<style lang="scss" scoped>
@import "~@/styles/mixin.scss";

.createPost-container {
  position: relative;

  .createPost-main-container {
    padding: 5px 45px 20px 50px;

    .postInfo-container {
      position: relative;
      @include clearfix;
      margin-bottom: 10px;

      .postInfo-container-item {
        float: left;
        //margin-top: 20px;
      }
    }
  }

  .word-counter {
    width: 40px;
    position: absolute;
    right: 10px;
    top: 0px;
  }
}

.article-textarea ::v-deep {
  textarea {
    margin: 10px 10px;
    padding-right: 50px;
    resize: none;
    border: none;
    border-radius: 0px;
    font-size: larger;
    border-bottom: 1px solid #bfcbd9;
  }
}
</style>
