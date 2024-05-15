<!--
  文章标签选择组件
  暂时弃用：需要解决子、父组件之间，即时传递数据的问题。
  问题同SelectCategories
-->
<template>
  <!--<el-select v-model="selectedTag" placeholder="Please Select Tags (Input As New Add IF Not Exists)"-->
  <!--           :filterable="true" :multiple="true"-->
  <!--           style="width: 100%;" @change="selectedTagHandler">-->
  <!--  <el-option :label="item.name" :value="item.id" v-for="item in tagListOptions" :key="item.id"></el-option>-->
  <!--</el-select>-->

  <el-select v-model="selectedTag" placeholder="Please Select Tags (Input As New Add IF Not Exists)"
             :filterable="true" :multiple="true"
             style="width: 100%;" value-key="id">
    <!--NOTICE：v-model绑定的是一个对象，option里面也要绑定一个对象--->
    <!--https://segmentfault.com/q/1010000019113355-->
    <el-option :label="tag.name" :value="tag" v-for="tag in tagListOptions" :key="tag.id"></el-option>
  </el-select>
</template>

<script>
import tagApi from "@/api/article/tag";

export default {
  name: "SelectTags",
  props: {
    initTags: {
      type: Array,
      default: function () {
        return {}
      }
    }
  },
  data() {
    return {
      //当前选择了那些文章标签
      selectedTag: [],
      //从后端获取所有的文章标签以供选择
      tagListOptions: [],
    }
  },
  emits: ['chooseTag'], //自定义事件，向父组件传递数据
  created() {
    //获取所有Tag
    this.getTags();
  },
  beforeUpdate() {
    //在编辑模式中，加载父组件传来的已选择的tags
    //console.log("this.initTags:", this.initTags)
    if(this.initTags.length > 0) {
      this.selectedTag = this.initTags
    }
  },
  methods: {
    //获取所有文章分类
    getTags() {
      tagApi.fetchAll().then(resp => {
        this.tagListOptions = []
        this.tagListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },
    selectedTagHandler() {
      //向父组件传递已经选择了的分类信息
      this.$emit('chooseTags', this.selectedTag);
      //console.log("selectedTagHandler: ", this.selectedTag)
    }
  }
}
</script>

<style scoped>

</style>