<!--
  文章标签选择组件
-->
<template>
  <el-select v-model="selectedTag" placeholder="Please Select Tags (Input As New Add IF Not Exists)"
             :allow-create="true" :filterable="true" :multiple="true" style="width: 100%;"
             @change="selectedTagHandler">
    <el-option :label="item.name" :value="item.id" v-for="item in tagListOptions" :key="item.id"></el-option>
  </el-select>
</template>

<script>
import tagApi from "@/api/article/tag";

export default {
  name: "SelectTags",
  props: ['initTags'],
  data() {
    return {
      //当前选择了那些文章分类
      selectedTag: [],
      //从后端获取所有的文章分类以供选择
      tagListOptions: [],
    }
  },
  emits: ['chooseTag'], //自定义事件，向父组件传递数据
  created() {
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
        this.tagListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },
    selectedTagHandler() {
      //向父组件传递已经选择了的分类信息
      this.$emit('chooseTag', this.selectedTag);
    }
  }
}
</script>

<style scoped>

</style>