<!--
  文章作者选择组件
-->
<template>
  <el-select v-model="selectedAuthor" placeholder="Please Select Authors"
             style="width: 100%;" :multiple="true" @change="selectedAuthorHandler">
    <el-option :label="item.nickName" :value="item.id" v-for="item in authorListOptions" :key="item.id"></el-option>
  </el-select>
</template>

<script>
import authorApi from "@/api/article/author";

export default {
  name: "SelectAuthor",
  props: ['initAuthors'],
  data() {
    return {
      //当前选择了那些文章分类
      selectedAuthor: [],
      //从后端获取所有的文章分类以供选择
      authorListOptions: [],
    }
  },
  emits: ['chooseAuthor'], //自定义事件，向父组件传递数据
  created() {
    this.getTags();
  },
  beforeUpdate() {
    if(this.initAuthors.length > 0) {
      this.selectedAuthor = this.initAuthors
    }
  },
  methods: {
    //获取所有文章分类
    getTags() {
      authorApi.fetchAll().then(resp => {
        //console.log("this.authorListOptions:", this.authorListOptions)
        this.authorListOptions = resp.data;
      }).catch(err => {
        console.log(err)
      })
    },
    selectedAuthorHandler() {
      //向父组件传递已经选择了的分类信息
      this.$emit('chooseAuthor', this.selectedAuthor);
    }
  }
}
</script>

<style scoped>

</style>